package reflectTest;

import org.junit.Test;

import javax.xml.bind.SchemaOutputResolver;
import java.lang.reflect.Method;

/**
 * Created by zwr on 2015/3/18.
 */
public class testBatch2Null {

    private void abc(String name){
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println(name);
        changeV(new String[]{name});
        System.out.println("+++++++++++");
        System.out.println(name);
    }

    private void  getMethods(){
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }

    private void changeV(String[] names){
        for (String name : names) {
            name = "abccc";
        }
    }

    private void getResult(){
        System.out.println("req_tag like '%\\" + 123 + "%' escape \'\\\'");
    }

    private void StringCon(){
        String cond = "REQ_TAG LIKE :REQ_TAG_0  AND CUR_STATUS IN  ( 105,107 )  order by CREATE_TIME DESC";
        StringBuffer sb = new StringBuffer();
        int breakPoint = cond.indexOf(":REQ_TAG_")+"REQ_TAG".length()+4;
        sb.append(cond.substring(0,breakPoint));
        String escapeStr = "{escape '!'}";
        sb.append(escapeStr);
        sb.append(cond.substring(breakPoint));
        cond = sb.toString();
        System.out.println(cond);
    }

    @Test
    public void testMain(){
        StringCon();
    }

}
