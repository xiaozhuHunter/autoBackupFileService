package org.hopxz.autobackup.server.message.xmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ParserXML {
    private HashMap<String,Object> hashMap = new HashMap<>();
    /*暂不支持循环嵌套报文的解析*/
    public HashMap<String,Object> parser(String xmlStr){
        HashMap<String,Object> xmlHashMap = new HashMap<>();
        /*解析xml报文，解析结果按照map数据类型返回*/
        xmlStr = xmlStr.replace("\t","");
        xmlStr = xmlStr.replace("\n","");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
            document.getDocumentElement().normalize();
            Node node = document.getFirstChild();
            xmlHashMap = getMapFromNode(node,"/"+node.getNodeName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlHashMap;
    }
    protected HashMap<String,Object> getMapFromNode(Node node,String pathStr){
        /*1、拆分xml报文
        * 2、支持循环，但是目前只支持单独循环，不支持循环嵌套*/
        NodeList nodeList = node.getChildNodes();
        if(nodeList.getLength() < 1 || nodeList.item(0).getNodeName().toString().equals("#text")){
            hashMap.put(pathStr,node.getTextContent());
        }else if(nodeList.getLength() >= 1){
            if(node.getNodeName().equals("array")) {
                getMapFromArrNode(node, pathStr);
            }else{
                for(int i=0;i<nodeList.getLength();i++){
                    reInokeMethod(nodeList.item(i),pathStr+"/"+nodeList.item(i).getNodeName());
                }
            }
        }else{
            reInokeMethod(node,pathStr);
        }
        return hashMap;
    }
    protected void reInokeMethod(Node node,String pathStr){
        String tempStr = pathStr;
        NodeList nodeList = node.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
            Node childNode = nodeList.item(i);
            if(pathStr != null && !"".equals(pathStr)) {
                pathStr = tempStr + "/"+ childNode.getNodeName();
            }else{
                pathStr = "/"+childNode.getNodeName();
            }
            getMapFromNode(childNode,pathStr);
        }
    }
    protected void getMapFromArrNode(Node node,String arrNodePath){
        NodeList nodeList = node.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
            ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
            HashMap<String,Object> gradeChildMap = new HashMap<>();
            Node childNode = nodeList.item(i);
            String tempStr = arrNodePath+childNode.getNodeName();
            if(hashMap.containsKey(tempStr)){
                arrayList=(ArrayList<HashMap<String,Object>>)hashMap.get(tempStr);
            }
            NodeList childNodeList = childNode.getChildNodes();
            for(int j=0;j<childNodeList.getLength();j++) {
                gradeChildMap.put(childNodeList.item(j).getNodeName(),childNodeList.item(j).getTextContent());
            }
            arrayList.add(gradeChildMap);
            hashMap.put(tempStr,arrayList);
        }
    }
}
