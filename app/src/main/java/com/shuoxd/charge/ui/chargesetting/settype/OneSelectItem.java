package com.shuoxd.charge.ui.chargesetting.settype;

import com.shuoxd.charge.ui.chargesetting.bean.BleSetBean;

import java.util.ArrayList;
import java.util.List;

public class OneSelectItem extends BleSetBean {


    public List<SelectItem> selectItems;
    public String oneChooseValue;
    public String dataValue;


    public static class SelectItem {
        public String title;
        public String value;

        public SelectItem() {
        }

        public SelectItem(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }


    public String[] getSelectItemTitles() {
        String[] titles=new String[selectItems.size()];
        for (int i = 0; i < selectItems.size(); i++) {
            titles[i]=selectItems.get(i).title;
        }
        return titles;
    }


    public int getIndexByValue(String value){
        int index=-1;
        if (selectItems!=null){
            for (int i = 0; i < selectItems.size(); i++) {
                String v = selectItems.get(i).value;
                if (v.equals(value)){
                    index=i;
                    break;
                }
            }
        }
        return index;
    }

}
