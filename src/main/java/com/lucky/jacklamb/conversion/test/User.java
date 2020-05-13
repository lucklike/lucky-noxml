package com.lucky.jacklamb.conversion.test;

import java.util.Arrays;
import java.util.List;

public class User {

    private int id;
    private String name;
    private Double math;
    private List<String> stringList;
    private String[] array;
    private List<TypeO> type0list;

    public List<TypeO> getType0list() {
        return type0list;
    }

    public void setType0list(List<TypeO> type0list) {
        this.type0list = type0list;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    private TypeO type;


    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public TypeO getType() {
        return type;
    }

    public void setType(TypeO type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMath() {
        return math;
    }

    public void setMath(Double math) {
        this.math = math;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", math=").append(math);
        sb.append(", stringList=").append(stringList);
        sb.append(", array=").append(Arrays.toString(array));
        sb.append(", type0list=").append(type0list);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
