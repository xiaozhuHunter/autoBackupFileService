package org.hopxz.autobackup.server.application.impl;

import org.hopxz.autobackup.server.common.utils.SQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class BaseApplicationAbstact implements BaseTriggerFunctionImpl{
    //
    private SQLUtils sqlUtils = new SQLUtils();
    //获取查询结果，结果按列模式返回
    protected HashMap<String,Object> getSelectResultsListByConditionAndColumnlist(String columnListStr,String tablename,String condition){
        HashMap<String,Object> hashMap = new HashMap();
        int num = getResultsNumByCondition(tablename,condition);
        hashMap.put("resultsTotalNum",num);
        if(num > 0) {
            ArrayList<HashMap<String, Object>> resultsArr = sqlUtils.getResultBySelect(columnListStr, tablename, condition);
            Set<String> keySets = resultsArr.get(0).keySet();
            Object[] columnObj = keySets.toArray();
            for (int i = 0; i < columnObj.length; i++) {
                setResultsIntoMap(i,num,hashMap,columnObj,resultsArr);
            }
        }
        return hashMap;
    }
    //若结果数量为1时，将HashMap中value的字符类型设为String；大于1时，设为ArrayList
    private void setResultsIntoMap(int index,int num,HashMap<String,Object> hashMap,
                                   Object[] columnObj,ArrayList<HashMap<String, Object>> resultsArr){
        if(num == 1){
            hashMap.put(columnObj[index].toString(),resultsArr.get(0).get(columnObj[index].toString()));
        }else{
            ArrayList<String> arrayList = new ArrayList<>();
            for (HashMap<String, Object> tempHashmap : resultsArr) {
                arrayList.add(tempHashmap.get(columnObj[index].toString()).toString());
            }
            hashMap.put(columnObj[index].toString(), arrayList);
        }
    }
    //获取查询结果总数量
    protected int getResultsNumByCondition(String tablename,String condition){
        int resultsNum = Integer.parseInt(sqlUtils.getResultBySelect("count(*) as countNum",tablename
                ,condition).get(0).get("countNum").toString());
        return resultsNum;
    }
    //获取分页查询结果,PageNum 当前页码，PageSize 每页结果数量
    protected HashMap<String,Object> getResultsSheetsByConditionAndColumnlist
    (String columnListStr,String tablename,String condition,int PageNum,int PageSize){
        String limitInfo = " limit "+((PageNum-1)*PageSize)+","+PageSize;
        HashMap<String,Object> hashMap = getSelectResultsListByConditionAndColumnlist(columnListStr,tablename,condition+limitInfo);
        hashMap.put("resultsTotalNum",getSelectResultsListByConditionAndColumnlist(columnListStr,tablename,condition).get("resultsTotalNum"));
        return hashMap;
    }
}
