/***
 * Function:used for storing Key-Value pair
 * Author:Ecky Leung
 * Coding Date:2018-4-18
 */
package cn.eckystudio.m.common;
import androidx.annotation.NonNull;

public class KeyValuePair<K extends Comparable,V> implements Comparable{
    private K mKey;
    private V mValue;

    public KeyValuePair(){

    }

    public KeyValuePair(K key,V value){
        this.mKey = key;
        this.mValue = value;
    }

    public K getKey(){
        return mKey;
    }

    public void setKey(K key){
        mKey = key;
    }

    public V getValue(){
        return mValue;
    }

    public void setValue(V value){
        mValue = value;
    }


    @Override
    public int compareTo(@NonNull Object o) {
        KeyValuePair kv = (KeyValuePair)o;
        return mKey.compareTo(kv.getKey());
    }
}
