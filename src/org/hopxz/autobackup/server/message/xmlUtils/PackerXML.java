package org.hopxz.autobackup.server.message.xmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class PackerXML {
    private int i = 2;//初始化循环参数
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private Element rootElement;//初始化根节点信息
    public String packer(HashMap<String,Object>hashMap){
        String xmlStr = "";
        String tempStr1="";
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();//构建xml报文
            Set<String> keySet = hashMap.keySet();
            for(int j=0;j< hashMap.size();j++){
                String pathStr =keySet.toArray()[j].toString();
                Object valueObj = hashMap.get(pathStr);
                if(pathStr.contains("array")){
                    tempStr1 = tempStr1 + arrBodyToString(pathStr.split("array")[1],valueObj);
                    pathStr = pathStr.split("array")[0]+"array";
                    valueObj = tempStr1;
                }
                buildXML(document,pathStr,valueObj);
            }
            xmlStr = xmlToString(document);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        //处理报文中的“<”和“>”在String类型中的转义
        xmlStr = xmlStr.replace("&lt;","<").replace("&gt;",">");
        return xmlStr;
    }
    protected String arrBodyToString(String pathStr,Object arrValueObj){
        //拼接循环数组报文结构体，作为array标签的值
        String tempStr1 = "";
        String leafStrHead = "";
        String leafStrFoot = "";
        ArrayList<HashMap<String,Object>> arrMapList = (ArrayList<HashMap<String, Object>>) arrValueObj;
        String[]pathArr = pathStr.split("/");
        for(String s:pathArr){
            if(s != null && !s.equals("")) {
                leafStrHead = "<" + s + ">" + leafStrHead;
                leafStrFoot = leafStrFoot + "</" + s + ">";
            }
        }
        for(HashMap<String,Object> hashMap:arrMapList){
            String listStr = "";
            Set<String> keySet = hashMap.keySet();
            for(int i=0;i< hashMap.size();i++){
                Object temp = keySet.toArray()[i];
                if(String.class.equals(hashMap.get(temp).getClass())){
                    String keyStr = temp.toString();
                    String valueStr = hashMap.get(temp).toString();
                    listStr = listStr +"<"+keyStr+">"+valueStr+"</"+keyStr+">";
                }
            }
            tempStr1 = leafStrHead+listStr+leafStrFoot;
        }
        return tempStr1;
    }
    protected String xmlToString(Document document){
        //将xml的doc对象转化为String
        String temp = "";
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            //xml transform String
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            transformer.transform(domSource, new StreamResult(byteArrayOutputStream));
            temp = byteArrayOutputStream.toString();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return temp;
    }
    protected void buildXML(Document document,String pathStr,Object valueObj) throws ParserConfigurationException, TransformerException {
        String[]arrStr = pathStr.split("\\/");
        if(document.getDocumentElement()==null) {
            rootElement = document.createElement(arrStr[1]);
            document.appendChild(rootElement);
        }
        if(String.class.equals(valueObj.getClass())) {
            addChildElement(document,rootElement, arrStr, valueObj.toString());
        }
    }
    protected void addChildElement(Document document,Element element,String[]arrStr,String valueStr){
        if(i >= arrStr.length){
            element.setTextContent(valueStr);
            i = 2;
        }else{
            Element element1 = null;
            int m = isExistedChildNode(element,arrStr[i]);
            if(m != -1) {
                element1 = (Element) element.getChildNodes().item(m);
            }else{
                element1 = document.createElement(arrStr[i]);
                element.appendChild(element1);
            }
            i++;
            addChildElement(document,element1,arrStr,valueStr);
        }
    }
    protected int isExistedChildNode(Element element,String nodeName){
        int num = -1;
        NodeList nodeList = element.getChildNodes();
        for(int n=0;n<nodeList.getLength();n++){
            if(nodeList.item(n).getNodeName().equals(nodeName)){
                num = n;
            }
        }
        return num;
    }
}
