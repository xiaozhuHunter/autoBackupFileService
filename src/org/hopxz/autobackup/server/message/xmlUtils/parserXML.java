package org.hopxz.autobackup.server.message.xmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class parserXML {
    /*暂不支持循环嵌套报文的解析*/
    private HashMap<String,Object>hashMap = new HashMap<>();
    public HashMap<String,Object> parser(String xmlStr){
        /*解析xml报文，解析结果按照map数据类型返回*/
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
            Element element = doc.getDocumentElement();
            String rootName = element.getTagName();
            getNodeNameAndValue(element,"/"+rootName);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return hashMap;
    }
    protected void getNodeNameAndValue(Element element, String nodePath){
        /*获取各个末端和循环节点名和节点字段的值*/
        NodeList nodeList = element.getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if(isXMLParentNode(node)){//轮询获取节点的子节点
                Element elementChild = (Element)node.getChildNodes();
                getNodeNameAndValue(elementChild,nodePath+"/"+nodeName);
            }else if(!node.getParentNode().getNodeName().equals("array")){//非循环部分节点拆分
                String elementValue = element.getElementsByTagName(nodeName).item(0).getTextContent();
                hashMap.put(nodePath+"/"+nodeName,elementValue);
            }else{//循环部分节点拆分
                String arrNodePath = nodePath+"/"+node.getNodeName();
                setHashMapKeyAndValueFromArrayBody(node,arrNodePath);
            }
        }
    }
    protected void setHashMapKeyAndValueFromArrayBody(Node arrNode,String arrNodePath){
        //1、对当前节点进行拆分，此方法只可用于循环节点内容
        //2、目前仅支持循环节点下的子节点为末端节点的拆分
        NodeList nodeList = arrNode.getChildNodes();
        HashMap<String,Object> hashMap1 = new HashMap<>();
        for(int i=0;i<nodeList.getLength();i++){
            Node tempNode = nodeList.item(i);
            String tempNodeName = tempNode.getNodeName();
            hashMap1.put(tempNodeName,
                    ((Element)arrNode).getElementsByTagName(tempNodeName).item(0).getTextContent());
        }
        if(hashMap.containsKey(arrNodePath)){
            Object obj = hashMap.get(arrNodePath);
            ((ArrayList<HashMap<String,Object>>)obj).add(hashMap1);
            hashMap.replace(arrNodePath,obj);
        }else{
            ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
            arrayList.add(hashMap1);
            hashMap.put(arrNodePath,arrayList);
        }
    }
    protected boolean isXMLParentNode(Node node){//判断节点是否有子节点
        boolean flag = true;
        NodeList nodeList = node.getChildNodes();
        if(node.getParentNode().getNodeName().equals("array") || node.getTextContent().equals("") ||
                nodeList.item(0).getNodeName().equals("#text")){
            flag = false;
        }
        return flag;
    }
}
