/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.test;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-28
 */
public class TestAny {


    public static void main(String args[]) {

        System.out.println(test());
    }

    public static boolean test(){
        try {
            System.out.println("xx");
            return true;
        }catch (Exception e){

        }finally {
            System.out.println("yy");
            return false;
        }
    }

}
