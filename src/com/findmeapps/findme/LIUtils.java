package com.findmeapps.findme;

import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Position;
import com.google.code.linkedinapi.schema.Positions;
import com.google.code.linkedinapi.schema.ThreePastPositions;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 3/11/12
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public class LIUtils {


    //TODO utils
    public static void toXml(Object bean) {
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().startsWith("get")) {
                try {
                    Object invoke = declaredMethod.invoke(bean);
                    System.out.println(declaredMethod.getName() + " = " + invoke);
                } catch (Exception e) {
                    e.printStackTrace();  //TODO To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public static void printPerson(Person profile) {
        toXml(profile);
        Positions positions = profile.getPositions();
        System.out.println("Positions:" + positions);
        if (positions != null && positions.getTotal() > 0) {
            List<Position> positionList = positions.getPositionList();
            for (Position position : positionList) {
                toXml(position);
            }
        }

        ThreePastPositions threePastPositions = profile.getThreePastPositions();
        System.out.println("threePastPositions:" + positions);
        if (threePastPositions != null && threePastPositions.getTotal() > 0) {
            List<Position> positionList = threePastPositions.getPositionList();
            for (Position position : positionList) {
                toXml(position);
            }
        }
    }
}
