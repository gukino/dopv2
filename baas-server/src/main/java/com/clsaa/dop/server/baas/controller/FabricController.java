package com.clsaa.dop.server.baas.controller;

import com.clsaa.dop.server.baas.Mapper.NetMapper;
import com.clsaa.dop.server.baas.Service.*;
import com.clsaa.dop.server.baas.model.dbMo.NetInfo;
import com.clsaa.dop.server.baas.model.jsonMo.jsonModel;
import com.clsaa.dop.server.baas.model.yamlMo.Orderer;
import com.clsaa.dop.server.baas.model.yamlMo.Organization;
import com.clsaa.dop.server.baas.model.yamlMo.Peer;
import com.clsaa.dop.server.baas.util.SFTPUtil;
import com.jcraft.jsch.SftpException;
import io.kubernetes.client.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 注释写在这
 *
 * @author Monhey
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration
public class FabricController {

    @Autowired
    FabricNetOperateService fabricNetOperateService;
    @Autowired
    JsonService jsonService;
    @Autowired
    FabricYamlGenerateService fabricYamlGenerateService;
    @Autowired
    NetMapper netMapper;
    @Autowired
    FabricK8sQueryService fabricK8sQueryService;
    @Autowired
    JenkinsService jenkinsService;
    @ApiOperation(value = "创建fabric网络", notes = "接口说明")
    @PostMapping("/v2/baas/createFabric")
    public String CreateFabric(@PathVariable(value = "netWorkJson") String netWorkJson) throws IOException, ApiException, SftpException {
        jsonModel jm = jsonService.CastJsonToNetBean(netWorkJson);
        String NameSpace = jm.getName();
        List<Orderer> orderList = jm.getOrder();
        List<Organization> orgList = jm.getOrg();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String current = sdf.format(date);
        NetInfo netInfo = new NetInfo();
        netInfo.setNetName(NameSpace);
        netInfo.setDescription(netWorkJson);
        netInfo.setStatus(2);
        netInfo.setCreateTime(current);
        netMapper.insertNet(netInfo);
        fabricYamlGenerateService.replaceWithConfigtxTemplate(orgList,orderList,jm.getConsensus());
        fabricYamlGenerateService.replaceWithCryptoconfigTemplate(orderList,orgList);
        SFTPUtil sftpUtil = new SFTPUtil();
        sftpUtil.login();
        File file = new File("src/main/resources/configtx.yaml");
        File file2 = new File("src/main/resources/crypto-config.yaml");
        InputStream is = new FileInputStream(file);
        InputStream is2 = new FileInputStream(file2);
        sftpUtil.upload("/mnt","nfsdata/fabric/"+jm.getName(), "configtx.yaml",is);
        sftpUtil.upload("/mnt","nfsdata/fabric/"+jm.getName(), "crypto-config.yaml",is2);
        sftpUtil.logout();
        String jobName =jenkinsService.createCreatFabricJob(NameSpace);
        jenkinsService.buildJob(jobName);
        try {
            fabricNetOperateService.createV1Namespace(NameSpace);
            fabricNetOperateService.createV1ConfigMap(NameSpace);
            for (Organization org : orgList) {
                List<Peer> peerList = org.getPeers();
                for (Peer p : peerList) {
                    fabricNetOperateService.createPeerDeployment(p, org.getOrgName(), NameSpace);
                    fabricNetOperateService.createPeerService(p, org.getOrgName(), NameSpace);
                    fabricNetOperateService.createPeerMetricService(p,org.getOrgName(),NameSpace);
                }
                fabricNetOperateService.createOrgCaDeployment(org.getOrgName(), NameSpace);
                fabricNetOperateService.createOrgCaService(org.getOrgName(), NameSpace);
                fabricNetOperateService.createCliDeployment(org.getOrgName(), NameSpace);
            }
            for (Orderer o : orderList) {
                fabricNetOperateService.createOrdererDeployment(o.getOrderName(), NameSpace);
                fabricNetOperateService.createOrdererService(o, NameSpace);
                fabricNetOperateService.createOrdererMetricService(o,NameSpace);
            }
            int NetId = netMapper.queryId(NameSpace);
            netMapper.updateNetStatu(1,NetId);
        }
        catch (ApiException e){
            System.err.println("Exception when calling CreateFabric");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();

            return "fail";
        }
        return "success";
    }


    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
    @GetMapping("/v2/baas/queryFabric/{id}")
    public String QueryFabricNetById( @PathVariable(value = "id") int id){
        NetInfo netInfo = netMapper.findNetById(id);
        return netInfo.toString();
    }


    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
    @GetMapping("/v2/baas/queryFabric")
    public List<NetInfo> QueryFabric(){
        List<NetInfo> netList = netMapper.getAllNet();
        return netList;
    }
    @ApiOperation(value = "删除网络",notes = "接口说明")
    @GetMapping("/v2/baas/deleteFabric/{id}")
    public String QueryFabric(@PathVariable(value = "id")int id){
        netMapper.updateNetStatu(0,id);
        return "success";
    }
//    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
//    @PostMapping("/v2/baas/insert")
//    public void insertNet(@RequestBody String netWorkJson){
//        jsonModel jm = jsonService.CastJsonToNetBean(netWorkJson);
//        String NameSpace = jm.getName();
//        NetInfo netInfo = new NetInfo();
//        netInfo.setNetName(NameSpace);
//        netInfo.setDescription(netWorkJson);
//        netMapper.insertNet(netInfo);
//        System.out.println(netWorkJson);
//    }
//    @ApiOperation(value = "根据查询fabric网络",notes = "接口说明")
//    @GetMapping("/v2/baas/queryNamespace/{namespace}")
//    public void find(@PathVariable(value = "namespace")String namespace) throws IOException, ApiException {
//        Map<String,String> map = fabricK8sQueryService.getNameSpacePodListStatu(namespace);
//        System.out.println(map.toString());
//    }

}