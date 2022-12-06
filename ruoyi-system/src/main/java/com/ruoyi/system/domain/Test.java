package com.ruoyi.system.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args){


        List result = new ArrayList();


        //发车
        List<Map> fache = new ArrayList();
        for(Iterator<Map> iterator = fache.iterator(); iterator.hasNext(); ) {
          Map next =(Map) iterator.next();

          for(Iterator<Map> resultIterator = result.iterator(); resultIterator.hasNext(); ) {
            Map map =(Map) resultIterator.next();
            //
          }

        }

    }

}
