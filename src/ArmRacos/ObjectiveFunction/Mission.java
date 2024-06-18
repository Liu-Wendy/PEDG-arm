package ArmRacos.ObjectiveFunction;

import ArmMPC.Automata;
import ArmMPC.Location;
import ArmMPC.Transition;
import ArmRacos.Componet.Dimension;
import ArmRacos.Componet.Instance;
import ArmRacos.Tools.ValueArc;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Mission implements Task{
    private Dimension dim;//the number of all parameters
    public ArrayList<Automata> automatas;
    public int autonum;
    public ArrayList<ArrayList<Integer>> combin;
    private FelEngine fel;
    private FelContext ctx;
    public ValueArc valueArc;
    //private int []path = new int[]{1,2,3,4,5,6,7,8,9};
    private double XtarMAX;
    private double RtarMAX;
    private double VMAX;
    public ArrayList<ArrayList<Double>> TIME;

    //private ArrayList<HashMap<String,Double>> allParametersValues1;
    //private ArrayList<HashMap<String,Double>> allParametersValues2;
    int matanum=2;
    public double delta=0.1;
    BufferedWriter bufferedWriter1;
    BufferedWriter bufferedWriter2;
    double[] timeProfile = new double[3];
    double[] singletimeProfile = new double[3];
    private boolean sat = true;
    private double globalPenalty = 0;
    private double cerr = 0.01;
    private double penalty = 0;
    boolean log_flag=false;
    InverseSolution IS;
    Random random=new Random();
    int stepnum=0;

    int paramSize;// one dimension for choosing path. Each command needs 9 arguments 4*9=36.
    int CommandSize ;
    ArrayList<ArrayList<String>>PathMap=new ArrayList<>();
    public ArrayList<ArrayList<String>> commands;
    public ArrayList<ArrayList<Integer>> path;

    HashMap<Integer,int[]> encodeMap;
    int choiceSize=39;
    public double[] initX=new double[]{208.67,208.67,300,300,208.67,500,500,480,600,650};
    public double[] initY=new double[]{0,100,0,200,400,100,400,80,250,350};
    public double[] initZ=new double[]{229.72,229.72,300,200,229.72,400,600,200,70,360};
    public double[] tarX=new double[]{10,10,200,300,90,600,505,500,630,680};
    public double[] tarY=new double[]{100,0,20,300,100,200,430,120,290,400};
    public double[] tarZ=new double[]{30,30,50,100,90,300,620,220,120,420};




    public Mission(int autonum){
        automatas=new ArrayList<>();
        this.autonum=autonum;
        IS=new InverseSolution();
        TIME=new ArrayList<>();

        XtarMAX=210;
        RtarMAX=180;
        VMAX=150;
        createMap();
        setCombin();
        dim=new Dimension();
        CommandSize=3*autonum;
        paramSize=1+9*(CommandSize-autonum);

        dim.setSize(paramSize);

        int index=0;
        while(index<paramSize){
            if(index<1){
                dim.setDimension(index,0,combin.size()-1,false);
                index++;
            }else{
                for(int j=0;j<CommandSize-autonum;j++){
                    int upIndex=index+3;
                    for(;index<upIndex;index++){
                        dim.setDimension(index,(-1)*XtarMAX,XtarMAX,true);
                    }
                    upIndex=index+3;
                    for(;index<upIndex;index++){
                        dim.setDimension(index,-RtarMAX,RtarMAX,true);
                    }
                    dim.setDimension(index++,16,VMAX,true);
                    dim.setDimension(index++,10,40,true);
                    dim.setDimension(index++,10,40,true);
                }
            }
        }

        fel = new FelEngineImpl();
        ctx = fel.getContext();
        valueArc = new ValueArc();

    }
    private void setCombin() {
        combin=new ArrayList<>();
        for(int i=0;i<PathMap.size();i++){
            ArrayList<Integer> tmp=new ArrayList<>();
            tmp.add(i);
            combin.add(tmp);
        }
        ArrayList<ArrayList<Integer>> pre=new ArrayList<>();
        int n=1;
        while (n<autonum){
            pre.clear();
            for(int i=0;i<combin.size();i++){
                ArrayList<Integer> current= combin.get(i);
                for(int j=0;j<PathMap.size();j++){
                    ArrayList<Integer> tmp= (ArrayList<Integer>) current.clone();
                    tmp.add(j);
                    pre.add(tmp);
                }
            }
            combin= (ArrayList<ArrayList<Integer>>) pre.clone();
            n++;
        }
    }

    private void createMap() {
        ArrayList<String> tmp=new ArrayList<>();
        tmp.add("fast");
        PathMap.add(tmp);//0

        tmp=new ArrayList<>();
        tmp.add("forward");
        PathMap.add(tmp);//1

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        PathMap.add(tmp);//2

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//3

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//4

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//5

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//6

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//7

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//8

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//9

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//10

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//11

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//12

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//13

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//14

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//15

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//16

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//17

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//18

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//19

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//20

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//21

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//22

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//23

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//24

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//25

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//26

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//27

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//28

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//29

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//30

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//31

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//32

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//33

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//34

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//35

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//36

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//37

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//38

    }

    public double[] getinsTime(){return timeProfile;}

    public void println1(String str) {
        try {
            bufferedWriter1.write(str + "\n");
        } catch (IOException e) {
            System.out.println("write to file error!");
        }
    }
    public void println2(String str) {
        try {
            bufferedWriter2.write(str + "\n");
        } catch (IOException e) {
            System.out.println("write to file error!");
        }
    }

    public void print1(String str) {
        try {
            bufferedWriter1.write(str);
        } catch (IOException e) {
            System.out.println("write to file error!");
        }
    }
    public void print2(String str) {
        try {
            bufferedWriter2.write(str);
        } catch (IOException e) {
            System.out.println("write to file error!");
        }
    }

    @Override
    public double getValue(Instance ins) {

        setAutomataByins(ins);

        //allParametersValues1 = new ArrayList<>();
        //allParametersValues2 = new ArrayList<>();
        ArrayList<ArrayList<Double>> args=new ArrayList<>();
        for(int n=0;n<autonum;n++){
            double temp1=0;
            ArrayList<String> command=commands.get(n);
            ArrayList<Double> tmp=new ArrayList<>();
            ArrayList<Double> time = TIME.get(n);
            for(int i=0;i< command.size()*3;i++){
                if(i%3==0&&i!=0){
                    temp1=tmp.get(i-1);
                }
                tmp.add(time.get(i)+temp1);
            }
            args.add(tmp);
        }

        sat=true;
        globalPenalty = 0;
        penalty=0;
        stepnum=0;
        for(int i=0;i<3;i++){
            singletimeProfile[i]=0;
        }



//
//
//            try {
//                bufferedWriter1 = new BufferedWriter(new FileWriter("log1.txt"));
//                bufferedWriter2 = new BufferedWriter(new FileWriter("log2.txt"));
//            }catch (IOException e) {
//                System.out.println("Open output.txt fail!");
//            }

        try{
            checkInvarientsByODE(args);
        } finally {
            try {
                if (bufferedWriter1 != null)
                    bufferedWriter1.close();

                if (bufferedWriter2 != null)
                    bufferedWriter2.close();
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }
        if(!sat) {
            if(penalty + globalPenalty == 0){
                //todo cfg file should have brackets
                System.out.println("penalty = 0 when unsat");
                System.exit(0);
            }
            double penAll = penalty + globalPenalty;
            if(penAll < valueArc.penAll) {
                valueArc.penalty = penalty;
                valueArc.globalPenalty = globalPenalty;
                valueArc.penAll = penAll;
            }
            return penAll*10000;
        }

        double timecost=0;
        for(int n=0;n<autonum;n++){
            timecost=Math.max(timecost,args.get(n).get(path.get(n).size()-1));
        }
//        if(timecost==4.3944) {
//                    int index=1;
//        for (int n=0;n<autonum;n++){
//            System.out.println("ARM"+Integer.toString(n+1)+":");
//            ArrayList<String> com=commands.get(n);
//            for(int ii=0;ii<com.size();ii++){
//                System.out.print(com.get(ii)+": ");
//                if(ii==com.size()-1){
//                    System.out.println(Double.toString(tarX[n])+" "+Double.toString(tarY[n])+" "+Double.toString(tarZ[n]));
//                }
//                else {
//                    System.out.print(ins.getFeature(index) + " " + ins.getFeature(index + 1) + " " + ins.getFeature(index + 2));
//
//                    switch (com.get(ii)) {
//                        case "forward" -> System.out.println(" " + ins.getFeature(index + 6));
//                        case "doorlike" -> System.out.println(" " + ins.getFeature(index + 7));
//                        default -> System.out.println(" ");
//                    }
//                    index+=9;
//                }
//            }
//        }
//        }
        if(Double.isNaN(timecost)){
            int a=0;
        }
        return timecost;
        //return computeValue(ins);
        //return valueArc.value;
    }
    public double computeValue(Instance ins){
        /*HashMap<String,Double> map = allParametersValues.get(allParametersValues.size() - 1);
        for(HashMap.Entry<String,Double> entry : map.entrySet()){
            ctx.set(entry.getKey(),entry.getValue());
        }
        ctx.set("target_x",automata.target_x);
        ctx.set("target_y",automata.target_y);
        Object obj = fel.eval(automata.obj_function);
        double value = 0;
        if(obj instanceof Double)
            value = (double)obj - 10000;
        else if(obj instanceof Integer){
            value = (int) obj - 10000;
        }
        else {
            System.err.println("error: result not of double!");
            System.out.println(obj);
            System.exit(-1);
        }*/
        double value = 0;
        if (value + 10000 < 0){
            System.exit(0);
        }
        if(value < valueArc.value){
            valueArc.value = value;
            //valueArc.allParametersValues = allParametersValues1.get(allParametersValues1.size()-1);
            valueArc.args = ins.getFeature();
        }
        return valueArc.value;
    }


    public HashMap<String, Double> cloneAllInitParametersValues(int i){
        HashMap<String, Double> newMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : automatas.get(i).initParameterValues.entrySet()) {
            if(i==0)
                newMap.put("a1_"+entry.getKey(), entry.getValue());
            else newMap.put("a2_"+entry.getKey(), entry.getValue());
        }

        return newMap;

    }

    public void checkAutomata(HashMap<String,Double> parametersValues,int index,int locIndex,int[] path){
        for(HashMap.Entry<String,Double> entry : parametersValues.entrySet()){
            ctx.set(entry.getKey(),entry.getValue());
        }

        for (int i = 0; i < automatas.get(index).locations.get(path[locIndex]).invariants.size(); ++i) {
            String expression=index==0?"a1_"+automatas.get(index).locations.get(path[locIndex]).invariants.get((i)):"a2_"+automatas.get(index).locations.get(path[locIndex]).invariants.get((i));
            boolean result = (boolean) fel.eval(expression);
            if (!result) {
                String invariant = index==0?"a1_"+automatas.get(index).locations.get(path[locIndex]).invariants.get(i):"a2_"+automatas.get(index).locations.get(path[locIndex]).invariants.get(i);
                if (computePenalty(invariant, false) < cerr)
                    continue;
                if (Double.isNaN(computePenalty(invariant, false))) {
                    sat = false;
                    penalty += 100000;
                } else {
                    sat = false;
                    //System.out.println(invariant);
                    penalty += computePenalty(invariant, false);
                }
            }
        }

    }
    public void updateFastInfo(ArrayList<HashMap<String,Double>> allmap,ArrayList<Integer> locIndexList) {
        for(int i=0;i<autonum;i++){
            int locIndex=locIndexList.get(i);
            if(locIndex<path.get(i).size()){
                String locName=automatas.get(i).locations.get(path.get(i).get(locIndex)).name;
                HashMap<String,Double> newMap1=allmap.get(i);
                if(locName.contains("fast")){
                    double A1_THETA1 = Math.toRadians(newMap1.get("theta1"));
                    double A1_THETA2 = Math.toRadians(newMap1.get("theta2"));
                    double A1_THETA3 = Math.toRadians(newMap1.get("theta3"));
                    double A1_THETA4 = Math.toRadians(newMap1.get("theta4"));
                    double A1_THETA5 = Math.toRadians(newMap1.get("theta5"));
                    double A1_THETA6 = Math.toRadians(newMap1.get("theta6"));

                    double x = Math.sin(A1_THETA1) * (-25.28 * Math.cos(A1_THETA5) * Math.sin(A1_THETA4) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA4) * Math.sin(A1_THETA5) - 10 * Math.cos(A1_THETA4) * Math.sin(A1_THETA6)) + Math.cos(A1_THETA1) * (29.69 + Math.sin(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10 * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10 * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))) + Math.cos(A1_THETA2) * (Math.cos(A1_THETA3) * (168.98 + 10 * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) - 25.28 * Math.sin(A1_THETA5)) + Math.sin(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10 * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))));
                    double y = Math.cos(A1_THETA1) * (25.28 * Math.cos(A1_THETA5) * Math.sin(A1_THETA4) + 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA4) * Math.sin(A1_THETA5) + 10. * Math.cos(A1_THETA4) * Math.sin(A1_THETA6)) + Math.sin(A1_THETA1) * (29.69 + Math.sin(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))) + Math.cos(A1_THETA2) * (Math.cos(A1_THETA3) * (168.98 + 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) - 25.28 * Math.sin(A1_THETA5)) + Math.sin(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))));
                    double z = 127. - 20. * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) + 25.28 * Math.cos(A1_THETA4) * Math.cos(A1_THETA5) * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) + 10. * Math.cos(A1_THETA4) * Math.cos(A1_THETA6) * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) * Math.sin(A1_THETA5) + Math.cos(A1_THETA3) * Math.sin(A1_THETA2) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) - 10. * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) * Math.sin(A1_THETA4) * Math.sin(A1_THETA6) + Math.cos(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6)));

                    newMap1.put("x1", x);
                    newMap1.put("x2", y);
                    newMap1.put("x3", z);
                }
            }

        }
    }

    public void updateForwardInfo(HashMap<String, Double> newMap, int locIndex, int autoIndex) {
        if(autoIndex==0){
            if(automatas.get(0).locations.get(locIndex+1).name.contains("forward_period1")){
                InverseSolution IS = new InverseSolution();

            }
        }
    }
    public void checkInvarientsByODE(ArrayList<ArrayList<Double>> argsList){
        double step=0;
        ArrayList<HashMap<String,Double>> allstepMap=new ArrayList<>();
        ArrayList<Integer> locIndexList=new ArrayList<>();
        ArrayList<Boolean> nextLoc=new ArrayList<>();
        double max_total=0;
        for(int n=0;n<autonum;n++){
            HashMap<String,Double> newMap = automatas.get(n).duplicateInitParametersValues();
            allstepMap.add(newMap);
            max_total=Math.max(max_total,argsList.get(n).get(argsList.get(n).size()-1));
            locIndexList.add(0);
            nextLoc.add(false);
        }

        updateFastInfo(allstepMap,locIndexList);
//        if(log_flag){
//            for(int n=0;n<autonum;n++){
//                logxyz(allstepMap.get(n),n);
//            }
//        }

        while(step<max_total) {
            double delta1=delta;
            for(int n=0;n<autonum;n++){
                if(locIndexList.get(n)<argsList.get(n).size())
                    delta1=Math.min(delta1,argsList.get(n).get(locIndexList.get(n))-allstepMap.get(n).get("t_current"));
            }
            for(int n=0;n<autonum;n++){
                if(locIndexList.get(n)<argsList.get(n).size()) {
                    double t = argsList.get(n).get(locIndexList.get(n)) - allstepMap.get(n).get("t_current");
                    if (delta1 == t) {
                        nextLoc.set(n, true);
                    }
                }
            }
            if(delta1!=0) {
                for (int n = 0; n < autonum; n++) {
                    int locIndex = locIndexList.get(n);
                    if(locIndex>=path.get(n).size()) continue;
                    HashMap<String, Double> newMap = allstepMap.get(n);
                    Automata automata = automatas.get(n);
                    ArrayList<Double> args = argsList.get(n);

                    newMap = computeValuesByFlow(newMap, n, locIndex, delta1, path.get(n));
                    allstepMap.set(n,newMap);
                    updateFastInfo(allstepMap,locIndexList);//fast mode need to calculate X
//                    logxyz(newMap1,1);

                }
            }

            for (int n = 0; n < autonum; n++) {
                if(nextLoc.get(n)){
                    int locIndex=locIndexList.get(n);
                    locIndexList.set(n,locIndex+1);
                    nextLoc.set(n,false);
                    //allParametersValues1.add(newMap1);
                    if(locIndex< path.get(n).size()-2) {
                        Transition transition = automatas.get(n).getTransitionBySourceAndTarget(path.get(n).get(locIndex), path.get(n).get(locIndex+1));
                        if (transition == null) {
                            System.out.println("Found no transition");
                            System.exit(-1);
                        }
                        for (HashMap.Entry<String, String> entry : transition.assignments.entrySet()) {
                            Object obj = fel.eval(entry.getValue());
                            double result = 0;
                            if (obj instanceof Integer) result = (int) obj;
                            else if (obj instanceof Double) result = (double) obj;
                            else {
                                System.out.println("Not Double and Not Integer!");
                                System.out.println(entry.getValue());
                                System.exit(0);
                            }

                            allstepMap.get(n).put(entry.getKey(), result);
                        }
                    }
                }
            }
            checkCombine(allstepMap);
            step+=delta1;

        }

    }

    public double checkCombine(ArrayList<HashMap<String,Double>> newMap){
        double res=0;
        for(int i=0;i< newMap.size();i++){
            for(int j=i+1;j< newMap.size();j++){
                HashMap<String,Double> newMap1=newMap.get(i);
                HashMap<String,Double> newMap2=newMap.get(j);
                res=Math.pow(newMap1.get("x1")-newMap2.get("x1"),2)+Math.pow(newMap1.get("x2")-newMap2.get("x2"),2)+Math.pow(newMap1.get("x3")-newMap2.get("x3"),2);
                if(res<0.01) {
                    sat=false;
                    globalPenalty+=res;
                }
            }
        }
        return res;
    }

    private void logxyz(HashMap<String, Double> newMap1,int index) {
        if(!log_flag) return;
        if(index==1){
            print1(Double.toString(newMap1.get("a1_x1"))+" ");
            print1(Double.toString(newMap1.get("a1_x2"))+" ");
            println1(Double.toString(newMap1.get("a1_x3")));
        }
        else {
            print2(Double.toString(newMap1.get("a2_x1"))+" ");
            print2(Double.toString(newMap1.get("a2_x2")+100)+" ");
            println2(Double.toString(newMap1.get("a2_x3")));
        }


    }


    public HashMap<String,Double> computeValuesByFlow(HashMap<String,Double> parametersValues,int index,int locIndex,double arg,ArrayList<Integer> pa){

        HashMap<String,Double> tempMap = (HashMap<String, Double>) parametersValues.clone();
        Location location= automatas.get(index).locations.get(pa.get(locIndex));
        for(HashMap.Entry<String,Double> entry : parametersValues.entrySet()){
            ctx.set(entry.getKey(),entry.getValue());
        }
        for(HashMap.Entry<String,String> entry:location.flows.entrySet()){
            String expression;
            if(entry.getKey().contains("omega")||entry.getKey().contains("v")){
                if(isdigit(entry.getValue().trim())){
                    expression = entry.getValue() ;
                }
                else expression = entry.getValue() ;

                Object obj = fel.eval(expression);
                double result;
                if(obj instanceof Double)
                    result = (double)obj;
                else if(obj instanceof Integer) {
                    result = (int) obj;
                }
                else if(obj instanceof Long){
                    result = ((Long)obj).doubleValue();
                }
                else {
                    result = 0;
                    System.out.println("Not Double and Not Integer!");
                    System.out.println(obj.getClass().getName());
                    System.out.println(obj);
                    System.out.println(location.flows.get(entry.getKey()));
                    System.exit(0);
                }
                double delta = result * arg;
                String var_name=entry.getKey();
                double res=parametersValues.get(var_name)+delta;
                tempMap.put(var_name,res);
                ctx.set(var_name,res);
            }

        }

        for(HashMap.Entry<String,String> entry:location.flows.entrySet()) {
            String expression;
            if (entry.getKey().contains("omega")||entry.getKey().contains("v")) {
            }else {
                if(isdigit(entry.getValue().trim())){
                    expression=entry.getValue();
                }else expression = entry.getValue() ;
                //double result=tempMap.get(expression);

                try{
                    Object obj = fel.eval(expression);
                }catch (Exception e){
                    System.out.println(expression);
                    System.out.println("error");
                }

                Object obj = fel.eval(expression);
                double result;
                if(obj instanceof Double)
                    result = (double)obj;
                else if(obj instanceof Integer) {
                    result = (int) obj;
                }
                else if(obj instanceof Long){
                    result = ((Long)obj).doubleValue();
                }
                else {
                    result = 0;
                    System.out.println("Not Double and Not Integer!");
                    System.out.println(obj.getClass().getName());
                    System.out.println(obj);
                    System.out.println(location.flows.get(entry.getKey()));
                    System.exit(0);
                }
                double delta = result * arg;
                String var_name=entry.getKey();
                double res=parametersValues.get(var_name) + delta;
                tempMap.put(var_name,res);
                ctx.set(var_name,res);

            }
        }
        String s="t_current";
        tempMap.put(s,tempMap.get(s)+arg);

        return tempMap;
    }

    public boolean isdigit(String s){
        for(int i=0;i<s.length();i++){
            if((s.charAt(i)>'9'||s.charAt(i)<'0')&&s.charAt(i)!='.'&&s.charAt(i)!='-'&&s.charAt(i)!='E') return false;
        }
        return true;
    }

    public double computeConstraintValue(String constraint){
        int firstRightBracket = constraint.trim().indexOf(")");
        if(firstRightBracket != -1 && constraint.indexOf('&') == -1 && constraint.indexOf('|') == -1)
            //return computePenalty(constraint.substring(constraint.indexOf('(')+1,constraint.lastIndexOf(")")),false);
            return computePenalty(constraint,false);
        if(firstRightBracket != -1 && firstRightBracket != constraint.length()-1){
            for(int i = firstRightBracket;i < constraint.length();++i){
                if(constraint.charAt(i) == '&'){
                    int index = 0;
                    int numOfBrackets = 0;
                    int partBegin = 0;
                    double pen = 0;
                    while(index < constraint.length()){
                        if(constraint.indexOf(index) == '(')
                            ++numOfBrackets;
                        else if(constraint.indexOf(index) == ')')
                            --numOfBrackets;
                        else if(constraint.indexOf(index) == '&' && numOfBrackets==0){
                            String temp = constraint.substring(partBegin,index);
                            boolean result = (boolean)fel.eval(temp);
                            if(!result) return 0;
                            else pen+= computeConstraintValue(temp);
                            index = index + 2;
                            partBegin = index;
                            constraint = constraint.substring(index);
                            continue;
                        }
                        ++index;
                    }
                    return pen;
                }
                else if(constraint.charAt(i) == '|'){
                    int index = 0;
                    int numOfBrackets = 0;
                    int partBegin = 0;
                    double minPen = Double.MAX_VALUE;
                    while(index < constraint.length()){
                        if(constraint.indexOf(index) == '(')
                            ++numOfBrackets;
                        else if(constraint.indexOf(index) == ')')
                            --numOfBrackets;
                        else if(constraint.indexOf(index) == '|' && numOfBrackets==0){
                            String temp = constraint.substring(partBegin,index);
                            boolean result = (boolean)fel.eval(temp);
                            if(result){
                                minPen = (computeConstraintValue(temp) < minPen) ? computeConstraintValue(temp) : minPen;
                            }
                            index = index + 2;
                            partBegin = index;
                            constraint = constraint.substring(index);
                            continue;
                        }
                        ++index;
                    }
                    return minPen;
                }
            }
        }
        else{
            if(firstRightBracket != -1){
                constraint = constraint.substring(constraint.indexOf('(')+1,firstRightBracket);
            }
            if(constraint.indexOf('&') != -1){
                String []strings = constraint.split("&");
                double pen = 0;
                for(int i = 0;i < strings.length;++i){
                    if(strings[i].equals("")) continue;
                    boolean result = (boolean)fel.eval(strings[i]);
                    if(!result) return 0;
                    else pen += computeConstraintValue(strings[i]);
                }
                return pen;
            }
            else if(constraint.indexOf('|') != -1){
                String []strings = constraint.split("\\|");
                double minPen = Double.MAX_VALUE;
                for(int i = 0;i < strings.length;++i){
                    if(strings[i].equals("")) continue;
                    boolean result = (boolean) fel.eval(strings[i]);
                    if(!result) continue;
                    else minPen = (computeConstraintValue(strings[i]) < minPen) ? computeConstraintValue(strings[i]) : minPen;
                }
                return minPen;
            }
            else return computePenalty(constraint,false);
        }
        return 0;
    }
    private double computePenalty(String expression,boolean isConstraint){
        if(isConstraint && expression.indexOf("|") != -1)
            return computePenaltyOfConstraint(expression);

        String []strings;
        String bigPart = "",smallPart = "";
        strings = expression.split("<=|<|>=|>|==");
        Object obj1 = fel.eval(strings[0].trim());
        Object obj2 = fel.eval(strings[1].trim());
        double big = 0,small = 0;
        if(obj1 instanceof Double)
            big = (double)obj1;
        else if(obj1 instanceof Integer) {
            big = (int) obj1;
            //System.out.println(entry.getKey() + " " + entry.getValue());
        }
        else {
            System.out.println("Not Double and Not Integer!");
            System.out.println(expression);
            System.out.println(obj1);
            System.out.println(obj1.getClass().getName());
            System.out.println("here");
            System.exit(0);
        }
        if(obj2 instanceof Double)
            small = (double)obj2;
        else if(obj2 instanceof Integer) {
            small = (int) obj2;
        }
        else if(obj2 instanceof Long){
            small = ((Long)obj2).doubleValue();
        }
        else {
            small = 0;
            System.out.println("Not Double and Not Integer!");
            System.exit(0);
        }
        return Math.abs(big-small);
    }
    private double computePenaltyOfConstraint(String expression){//just one level
        String []expressions = expression.split("\\|");
        double result = Double.MAX_VALUE;
        for(String string:expressions){
            if(string.length()<=0)  continue;
            double temp = computePenalty(string,false);
            result = (temp < result) ? temp : result;
        }
        return result;
    }


    @Override
    public Dimension getDim() {
        return dim;
    }
    public  void setlogFlag(){
        log_flag=true;
    }

    public int setAutomataByins(Instance ins){

        automatas.clear();
        TIME.clear();

        fillinCommands(ins);
        clearAutomata();

        int current_index=1;

        //InverseSolution IS=new InverseSolution();
        double[] X_tar=new double[3];
        double[] R_tar=new double[3];
        double[] current_Theta=new double[6];//current theta
        double[] current_X=new double[3];
        double[] current_R=new double[3];
        double[] delta_X=new double[3];
        double[] delta_R=new double[3];
        double[] theta=new double[6];
        double[] X=new double[3];
        double[] R=new double[3];
        double lastTime=0;

        for(int i=0;i< automatas.size();i++) {
            boolean first_forward=true;
            boolean first_fast=true, first_doorlike=true;
            int locIndex;
            int len=(commands.get(i).size()-1)*3;
            lastTime=0;
            ArrayList<Double> time=new ArrayList<>();
            for(locIndex = 0;locIndex < len;++locIndex){
                if(locIndex==0){
                    for(int index=0;index<6;index++){
                        current_Theta[index]=automatas.get(i).initParameterValues.get("theta"+Integer.toString(index+1));
                    }
                    //current_X=getFastInfo();
                    for(int index=0;index<3;index++) {
                        current_X[index]=automatas.get(i).initParameterValues.get("x"+Integer.toString(index+1));
                        current_R[index]=automatas.get(i).initParameterValues.get("r"+Integer.toString(index+1));
                    }
                }
                else {
                    current_Theta=theta.clone();
                    current_X=X.clone();
                    current_R=R.clone();
                }
                String name=automatas.get(i).locations.get(locIndex+1).name;

                if(name.contains("fast")){
                    if(!first_fast) {
                        if(name.contains("period3")){
                            first_fast=true;
                        }
                        continue;
                    }

                    X_tar[0]=ins.getFeature(current_index)+current_X[0];
                    X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                    X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                    current_index+=3;

                    R_tar[0]=ins.getFeature(current_index);
                    R_tar[1]=ins.getFeature(current_index+1);
                    R_tar[2]=ins.getFeature(current_index+2);
                    current_index+=3;

                    //Calculate inverse solution for theta
                    double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    /*if(theta_tar[0]==1000){
                        no_solution++;
                        if(i==0) return 1;
                        else return 2;
                    }*/
                    while (theta_tar[0]==1000||X_tar[2]<0){
                        current_index-=6;
                        for (int k = 0; k < 6; k++) {
                            ins.setFeature(current_index+k, dim.getRegion(current_index+k)[0]+random.nextDouble()*(dim.getRegion(current_index+k)[1]-dim.getRegion(current_index+k)[0]));
                        }

                        X_tar[0]=ins.getFeature(current_index)+current_X[0];
                        X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                        X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                        current_index+=3;

                        R_tar[0]=ins.getFeature(current_index);
                        R_tar[1]=ins.getFeature(current_index+1);
                        R_tar[2]=ins.getFeature(current_index+2);
                        current_index+=3;

                        //Calculate inverse solution for theta
                        theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    }
                    double[] delta_theta=new double[6];
                    for(int j=0;j<6;j++) {
                        delta_theta[j] = theta_tar[j] - current_Theta[j];
                    }
                    double[] t=IS.Solve_T12(delta_theta);
                    double[] a_fast=IS.a_fast;
                    double[] w_fast=IS.w_fast;
                    for(int m=0;m<3;m++){
                        time.add(t[m]);
                    }
                    //double[] omega_bar=IS.Solve_omega_bar(current_Theta,theta_tar);
                    //Time.add(IS.Solve_T12(delta_theta));

                    //TODO calculate invarients/flow/guard... with theta_tar and omega_bar-finish
                    setFast(t,lastTime,i,locIndex,delta_theta,a_fast,w_fast);

                    theta=theta_tar.clone();
                    X=X_tar.clone();
                    R=R_tar.clone();
                    first_fast=false;
                    lastTime+=t[2];
                    current_index+=3;
//                    System.out.print("fast:  ");
//                    for(int m=0;m<3;m++){
//                        System.out.print(X_tar[m]+"  ");
//                    }
//                    System.out.println(" ");

                }else if(name.contains("forward")){
                    if(!first_forward) {
                        if(name.contains("period3")){
                            first_forward=true;
                        }
                        continue;
                    }
                    //first_forward=true;
                    X_tar[0]=ins.getFeature(current_index)+current_X[0];
                    X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                    X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                    current_index+=3;

                    R_tar[0]=ins.getFeature(current_index);
                    R_tar[1]=ins.getFeature(current_index+1);
                    R_tar[2]=ins.getFeature(current_index+2);
                    current_index+=3;

                    double V_tar=ins.getFeature(current_index);
                    current_index+=1;

                    double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    while (theta_tar[0]==1000||X_tar[2]<0){
                        current_index-=7;
                        for (int k = 0; k < 7; k++) {
                            ins.setFeature(current_index+k, dim.getRegion(current_index+k)[0]+random.nextDouble()*(dim.getRegion(current_index+k)[1]-dim.getRegion(current_index+k)[0]));
                        }

                        X_tar[0]=ins.getFeature(current_index)+current_X[0];
                        X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                        X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                        current_index+=3;

                        R_tar[0]=ins.getFeature(current_index);
                        R_tar[1]=ins.getFeature(current_index+1);
                        R_tar[2]=ins.getFeature(current_index+2);
                        current_index+=3;

                        V_tar=ins.getFeature(current_index);
                        current_index+=1;

                        //Calculate inverse solution for theta
                        theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    }

                    for(int j=0;j<3;j++) {
                        delta_X[j] = X_tar[j] - current_X[j];
                    }
                    double[] t=IS.Solve_T12_forward(delta_X,V_tar);
                    for(int m=0;m<3;m++){
                        time.add(t[m]);
                    }
                    //Time.add(IS.Solve_T12_forward(delta_X,V_tar));
                    double[] a_forward=IS.a_forward;
                    double[] v_forward=IS.v_forward;
                    //TODO calculate invarients/flow/guard... with theta_tar and omega_bar
                    setForward(t,lastTime,i,locIndex,theta_tar,a_forward,v_forward);

                    theta=theta_tar.clone();
                    X=X_tar.clone();
                    R=R_tar.clone();
                    first_forward=false;
                    lastTime+=t[2];
                    current_index+=2;

//                    System.out.print("forward:  ");
//                    for(int m=0;m<3;m++){
//                        System.out.print(X_tar[m]+"  ");
//                    }
//                    System.out.println(" ");


                }else if(name.contains("doorlike")){
                    if(!first_doorlike) {
                        if(name.contains("period3")){
                            first_doorlike=true;
                        }
                        continue;
                    }

                    X_tar[0]=ins.getFeature(current_index)+current_X[0];
                    X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                    X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                    current_index+=3;

                    R_tar[0]=ins.getFeature(current_index);
                    R_tar[1]=ins.getFeature(current_index+1);
                    R_tar[2]=ins.getFeature(current_index+2);
                    current_index+=4;

                    double Height=ins.getFeature(current_index);
                    current_index+=1;

                    double[] tmp_tar1=new double[]{current_X[0],current_X[1],current_X[2]+Height};
                    double[] tmp_tar2=new double[]{X_tar[0],X_tar[1],X_tar[2]+Height};

                    double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    while (theta_tar[0]==1000||X_tar[2]<0){
                        current_index-=8;
                        for (int k = 0; k < 7; k++) {
                            ins.setFeature(current_index+k, dim.getRegion(current_index+k)[0]+random.nextDouble()*(dim.getRegion(current_index+k)[1]-dim.getRegion(current_index+k)[0]));
                        }

                        X_tar[0]=ins.getFeature(current_index)+current_X[0];
                        X_tar[1]=ins.getFeature(current_index+1)+current_X[1];
                        X_tar[2]=ins.getFeature(current_index+2)+current_X[2];
                        current_index+=3;

                        R_tar[0]=ins.getFeature(current_index);
                        R_tar[1]=ins.getFeature(current_index+1);
                        R_tar[2]=ins.getFeature(current_index+2);
                        current_index+=4;

                        Height=ins.getFeature(current_index);
                        current_index+=1;

                        tmp_tar1=new double[]{current_X[0],current_X[1],current_X[2]+Height};
                        tmp_tar2=new double[]{X_tar[0],X_tar[1],X_tar[2]+Height};

                        //Calculate inverse solution for theta
                        theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                    }

                    for(int j=0;j<3;j++) {
                        delta_X[j] = tmp_tar2[j] - tmp_tar1[j];
                    }
                    double[] t=IS.Solve_T12_doorlike(delta_X,Height);
                    for(int m=0;m<3;m++){
                        time.add(t[m]);
                    }
                    double[] v_doorlike=IS.v_doorlike;
                    setDoorlike(t,lastTime,i,locIndex,theta_tar,v_doorlike, IS.v_max);

                    theta=theta_tar.clone();
                    X=X_tar.clone();
                    R=R_tar.clone();
                    first_doorlike=false;
                    lastTime+=t[2];
                    current_index+=1;
//                    System.out.print("doorlike:  ");
//                    for(int m=0;m<3;m++){
//                        System.out.print(X_tar[m]+"  ");
//                    }
//                    System.out.println(" ");
                }

            }

            //last moving to given target point
            //arm1:[20,50,150,0,-25,-90]arm2:[30,-50,150,0,-25,-90]
            if(len==0){
                for(int index=0;index<6;index++){
                    current_Theta[index]=automatas.get(i).initParameterValues.get("theta"+Integer.toString(index+1));
                }
                //current_X=getFastInfo();
                for(int index=0;index<3;index++) {
                    current_X[index]=automatas.get(i).initParameterValues.get("x"+Integer.toString(index+1));
                    current_R[index]=automatas.get(i).initParameterValues.get("r"+Integer.toString(index+1));
                }
            }
            else {
                current_Theta=theta.clone();
                current_X=X.clone();
                current_R=R.clone();
            }
            String name=automatas.get(i).locations.get(locIndex+1).name;

            if(name.contains("fast")){
                X_tar[0] = tarX[i];
                X_tar[1] = tarY[i];
                X_tar[2] = tarZ[i];

                R_tar[0] = 1;
                R_tar[1] = 2;
                R_tar[2] = 3;

                double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);

                double[] delta_theta=new double[6];
                for(int j=0;j<6;j++) {
                    delta_theta[j] = theta_tar[j] - current_Theta[j];
                }
                double[] t=IS.Solve_T12(delta_theta);
                double[] a_fast=IS.a_fast;
                double[] w_fast=IS.w_fast;
                for(int m=0;m<3;m++){
                    time.add(t[m]);
                }
                setFast(t,lastTime,i,locIndex,delta_theta,a_fast,w_fast);

//                System.out.print("fast:  ");
//                for(int m=0;m<3;m++){
//                    System.out.print(X_tar[m]+"  ");
//                }
//                System.out.println(" ");

            }
            else if(name.contains("forward")){
                X_tar[0] = tarX[i];
                X_tar[1] = tarY[i];
                X_tar[2] = tarZ[i];

                R_tar[0] = 1;
                R_tar[1] = 2;
                R_tar[2] = 3;

                double V_tar=20;
                double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                for(int j=0;j<3;j++) {
                    delta_X[j] = X_tar[j] - current_X[j];
                }
                double[] t=IS.Solve_T12_forward(delta_X,V_tar);
                for(int m=0;m<3;m++){
                    time.add(t[m]);
                }
                //Time.add(IS.Solve_T12_forward(delta_X,V_tar));
                double[] a_forward=IS.a_forward;
                double[] v_forward=IS.v_forward;
                setForward(t,lastTime,i,locIndex,theta_tar,a_forward,v_forward);
//                System.out.print("forward:  ");
//                for(int m=0;m<3;m++){
//                    System.out.print(X_tar[m]+"  ");
//                }
//                System.out.println(" ");
            }
            else if(name.contains("doorlike")){
                X_tar[0] = tarX[i];
                X_tar[1] = tarY[i];
                X_tar[2] = tarZ[i];

                R_tar[0] = 1;
                R_tar[1] = 2;
                R_tar[2] = 3;

                double Height=10;
                double[] tmp_tar1=new double[]{current_X[0],current_X[1],current_X[2]+Height};
                double[] tmp_tar2=new double[]{X_tar[0],X_tar[1],X_tar[2]+Height};

                double[] theta_tar=IS.F_inverse(X_tar,R_tar,current_Theta);
                for(int j=0;j<3;j++) {
                    delta_X[j] = tmp_tar2[j] - tmp_tar1[j];
                }
                double[] t=IS.Solve_T12_doorlike(delta_X,Height);
                for(int m=0;m<3;m++){
                    time.add(t[m]);
                }
                double[] v_doorlike=IS.v_doorlike;
                setDoorlike(t,lastTime,i,locIndex,theta_tar,v_doorlike, IS.v_max);
//                System.out.print("doorlike:  ");
//                for(int m=0;m<3;m++){
//                    System.out.print(X_tar[m]+"  ");
//                }
//                System.out.println(" ");
            }
            TIME.add(time);
        //todo set last move

        }
        return 0;



    }

    private void fillinCommands(Instance ins) {
        commands=new ArrayList<>();
        path=new ArrayList<>();
        ArrayList<Integer> choice=combin.get((int)ins.getFeature(0));
        for(int i=0;i<autonum;i++){
            ArrayList<String> command=PathMap.get(choice.get(i));
            commands.add(command);
            ArrayList<Integer> p=new ArrayList<>();
            for(int j=0;j<command.size()*3;j++){
                p.add(j+1);
            }
            path.add(p);
        }

    }


    private double[] getFastInfo() {
        double A1_THETA1 = 0;
        double A1_THETA2 = 0;
        double A1_THETA3 = 0;
        double A1_THETA4 = 0;
        double A1_THETA5 = 0;
        double A1_THETA6 = 0;
        //double x=Math.sin(A1_THETA1)*(-25.28*Math.cos(A1_THETA5)*Math.sin(A1_THETA4)-10*Math.cos(A1_THETA6)*Math.sin(A1_THETA4)*Math.sin(A1_THETA5)-10*Math.cos(A1_THETA4)*Math.sin(A1_THETA6))+Math.cos(A1_THETA1)*(29.69+Math.sin(A1_THETA3)*(108.+Math.sin(A1_THETA3)*(-168.98-10*Math.cos(A1_THETA5)*Math.cos(A1_THETA6)+25.28*Math.sin(A1_THETA5))+Math.cos(A1_THETA3)*(20+Math.cos(A1_THETA4)*(-25.28*Math.cos(A1_THETA5)-10*Math.cos(A1_THETA6)*Math.sin(A1_THETA5))+10*Math.sin(A1_THETA4)*Math.sin(A1_THETA6)))+Math.cos(A1_THETA3)*(Math.cos(A1_THETA3)*(168.98+10*Math.cos(A1_THETA5)*Math.cos(A1_THETA6)-25.28*Math.sin(A1_THETA5))+Math.sin(A1_THETA3)*(20+Math.cos(A1_THETA4)*(-25.28*Math.cos(A1_THETA5)-10*Math.cos(A1_THETA6)*Math.sin(A1_THETA5))+10*Math.sin(A1_THETA4)*Math.sin(A1_THETA6))))
        double x = Math.sin(A1_THETA1) * (-25.28 * Math.cos(A1_THETA5) * Math.sin(A1_THETA4) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA4) * Math.sin(A1_THETA5) - 10 * Math.cos(A1_THETA4) * Math.sin(A1_THETA6)) + Math.cos(A1_THETA1) * (29.69 + Math.sin(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10 * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10 * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))) + Math.cos(A1_THETA2) * (Math.cos(A1_THETA3) * (168.98 + 10 * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) - 25.28 * Math.sin(A1_THETA5)) + Math.sin(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10 * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10 * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))));
        double y = Math.cos(A1_THETA1) * (25.28 * Math.cos(A1_THETA5) * Math.sin(A1_THETA4) + 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA4) * Math.sin(A1_THETA5) + 10. * Math.cos(A1_THETA4) * Math.sin(A1_THETA6)) + Math.sin(A1_THETA1) * (29.69 + Math.sin(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))) + Math.cos(A1_THETA2) * (Math.cos(A1_THETA3) * (168.98 + 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) - 25.28 * Math.sin(A1_THETA5)) + Math.sin(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6))));
        double z = 127. - 20. * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) + 25.28 * Math.cos(A1_THETA4) * Math.cos(A1_THETA5) * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) + 10. * Math.cos(A1_THETA4) * Math.cos(A1_THETA6) * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) * Math.sin(A1_THETA5) + Math.cos(A1_THETA3) * Math.sin(A1_THETA2) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) - 10. * Math.sin(A1_THETA2) * Math.sin(A1_THETA3) * Math.sin(A1_THETA4) * Math.sin(A1_THETA6) + Math.cos(A1_THETA2) * (108. + Math.sin(A1_THETA3) * (-168.98 - 10. * Math.cos(A1_THETA5) * Math.cos(A1_THETA6) + 25.28 * Math.sin(A1_THETA5)) + Math.cos(A1_THETA3) * (20. + Math.cos(A1_THETA4) * (-25.28 * Math.cos(A1_THETA5) - 10. * Math.cos(A1_THETA6) * Math.sin(A1_THETA5)) + 10. * Math.sin(A1_THETA4) * Math.sin(A1_THETA6)));

        return new double[]{x,y,z};
    }

    public void clearAutomata() {
        for(int i = 0; i < autonum ; i++) {
            Automata temp=new Automata();
            temp.parameters = new ArrayList<>();
            temp.locations = new HashMap<>();
            temp.transitions = new ArrayList<>();
            temp.initParameterValues=new HashMap<>();
            for(int j=0;j<6;j++){
                temp.parameters.add(j,"theta"+Integer.toString(j+1));
                temp.parameters.add(j,"omega"+Integer.toString(j+1));
                temp.initParameterValues.put("theta"+Integer.toString(j+1),0.0);
                temp.initParameterValues.put("omega"+Integer.toString(j+1),0.0);
            }
            temp.initParameterValues.put("x"+Integer.toString(1),initX[i]);
            temp.initParameterValues.put("x"+Integer.toString(2),initY[i]);
            temp.initParameterValues.put("x"+Integer.toString(3),initZ[i]);
            for(int j=0;j<3;j++){
                temp.parameters.add("x"+Integer.toString(j+1));
                temp.parameters.add("v"+Integer.toString(j+1));
                temp.initParameterValues.put("v"+Integer.toString(j+1),0.0);
                temp.initParameterValues.put("r"+Integer.toString(j+1),0.0);
            }
            temp.parameters.add("t_current");
            temp.initParameterValues.put("t_current",0.0);
            int locNum=0;
            int lastLocNum=-1;
            ArrayList<String> tmp=commands.get(i);

            for (int index =0 ; index < tmp.size(); index++) {
                String command = tmp.get(index);
                for(int k=0;k<3;k++) {
                    locNum++;
                    Location location = new Location(locNum, command+"_period"+Integer.toString(k+1));
                    temp.locations.put(location.getNo(),location);
                    //set transition
                    if(lastLocNum!=-1){
                        Transition transition = new Transition(lastLocNum, locNum);
                        temp.locations.get(lastLocNum).addNeibour(locNum);
                        temp.transitions.add(transition);
                    }
                    lastLocNum=locNum;
                }
//                if (command.contains("fast")) {
//                    //set locations
//                    for(int k=0;k<3;k++) {
//                        locNum++;
//                        Location location = new Location(locNum, "fast_period"+Integer.toString(k+1));
//                        temp.locations.put(location.getNo(),location);
//                        //set transition
//                        if(lastLocNum!=-1){
//                            Transition transition = new Transition(lastLocNum, locNum);
//                            temp.locations.get(lastLocNum).addNeibour(locNum);
//                            temp.transitions.add(transition);
//                        }
//                        lastLocNum=locNum;
//                    }
//
//
//
//                }else if(command.contains("forward")){
//                    for(int k=0;k<3;k++) {
//                        locNum++;
//                        Location location = new Location(locNum, "forward_period"+Integer.toString(k+1));
//                        temp.locations.put(location.getNo(),location);
//                        if(lastLocNum!=-1){
//                            Transition transition = new Transition(lastLocNum, locNum);
//                            temp.locations.get(lastLocNum).addNeibour(locNum);
//                            temp.transitions.add(transition);
//                        }
//                        lastLocNum=locNum;
//                    }
//                }else if(command.contains("doorlike")){
//                    for(int k=0;k<3;k++) {
//                        locNum++;
//                        Location location = new Location(locNum, "doorlike_period"+Integer.toString(k+1));
//                        temp.locations.put(location.getNo(),location);
//                        if(lastLocNum!=-1){
//                            Transition transition = new Transition(lastLocNum, locNum);
//                            temp.locations.get(lastLocNum).addNeibour(locNum);
//                            temp.transitions.add(transition);
//                        }
//                        lastLocNum=locNum;
//                    }
//
//                }else{
//                    //todo other modes
//                }
            }
            automatas.add(temp);
        }
        //String tmp="(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))*(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))+(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))- (24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-0)*(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))-(24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-0)+(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))*(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))<1";
        //String tmp="(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))*(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))+(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))- (24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-207)*(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))-(24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-207)+(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))*(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))<5184";
        //String tmp="(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))*(-24.28*sin(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+cos(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5)))) - (-24.28*sin(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+cos(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5))))))+(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))- (24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-414)*(24.28*cos(a1_theta1)*sin(a1_theta4)*sin(a1_theta5)+sin(a1_theta1)*(29.69+cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))*sin(a1_theta2)-20.0*sin(a1_theta2)*sin(a1_theta3)+24.28*cos(a1_theta4)*sin(a1_theta2)*sin(a1_theta3)*sin(a1_theta5)+cos(a1_theta2)*(108.0+(-168.98-24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(20.0-24.28*cos(a1_theta4)*sin(a1_theta5))))-(24.28*cos(a2_theta2)*sin(a2_theta4)*sin(a2_theta5)+sin(a2_theta2)*(29.69+cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))*sin(a2_theta2)-20.0*sin(a2_theta2)*sin(a2_theta3)+24.28*cos(a2_theta4)*sin(a2_theta2)*sin(a2_theta3)*sin(a2_theta5)+cos(a2_theta2)*(108.0+(-168.98-24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(20.0-24.28*cos(a2_theta4)*sin(a2_theta5)))))-414)+(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))*(127.0+sin(a1_theta2)*(-108.0+(168.98+24.28*cos(a1_theta5))*sin(a1_theta3)+cos(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5)))+cos(a1_theta2)*(cos(a1_theta3)*(-168.98-24.28*cos(a1_theta5))+sin(a1_theta3)*(-20.0+24.28*cos(a1_theta4)*sin(a1_theta5))) - (127.0+sin(a2_theta2)*(-108.0+(168.98+24.28*cos(a2_theta5))*sin(a2_theta3)+cos(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))+cos(a2_theta2)*(cos(a2_theta3)*(-168.98-24.28*cos(a2_theta5))+sin(a2_theta3)*(-20.0+24.28*cos(a2_theta4)*sin(a2_theta5)))))<10000";
//        String tmp="(a1_x1-a2_x1)*(a1_x1-a2_x1)+(a1_x2-a2_x2-101)*(a1_x2-a2_x2-101)+(a1_x3-a2_x3)*(a1_x3-a2_x3)<20000";
//        tmp = tmp.replace("pow", "$(Math).pow");
//        tmp = tmp.replace("sin", "$(Math).sin");
//        tmp = tmp.replace("cos", "$(Math).cos");
//        tmp = tmp.replace("tan", "$(Math).tan");
//        tmp = tmp.replace("sqrt", "$(Math).sqrt");
//        automatas.get(0).forbiddenConstraints=tmp;
//        automatas.get(1).forbiddenConstraints=tmp;
    }


    void setFast(double[] T12,double lastTime, int Auto_index,int locIndex,double[] delta_theta,double[] a_fast,double[] w_fast){
        //T12[0]=tamx,T12[0]=t2
        double t1=T12[0]+lastTime;
        double t2=T12[1]+lastTime;
        double t3=T12[2]+lastTime;
        if(locIndex!=0){
//            Transition transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(path.get(Auto_index).get(locIndex),path.get(Auto_index).get(locIndex+1));

            Transition transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex-1,locIndex);
            //todo fast mode is not the first mode
        }
        String tmp1="";
        String tmp2="";
        String tmp3="";
        for(int i=0;i<6;i++){
            tmp1=tmp1+"omega"+Integer.toString(i+1)+"'="+Double.toString(a_fast[i])+"&amp;";
            tmp2=tmp2+"theta"+Integer.toString(i+1)+"'="+Double.toString(w_fast[i])+"&amp;";
            tmp3=tmp3+"omega"+Integer.toString(i+1)+"'="+Double.toString((-1)*a_fast[i])+"&amp;";
        }
        Location location=automatas.get(Auto_index).locations.get(locIndex+1);
        location.setVariant("t_current<="+t1,automatas.get(Auto_index).parameters);
        location.setFlow("theta1'=omega1  &amp; theta2'=omega2 &amp;  theta3'=omega3 &amp; theta4'=omega4 &amp; theta5'=omega5 &amp; theta6'=omega6&amp;"+tmp1,automatas.get(Auto_index).parameters);
        Transition transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+1,locIndex+2);
        transition.setGuard("t=="+t1,automatas.get(Auto_index).parameters);


        location=automatas.get(Auto_index).locations.get(locIndex+2);
        location.setVariant("t_current<="+t2,automatas.get(Auto_index).parameters);
        location.setFlow(tmp2,automatas.get(Auto_index).parameters);
        transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+2,locIndex+3);
        transition.setGuard("t=="+t2,automatas.get(Auto_index).parameters);

        location=automatas.get(Auto_index).locations.get(locIndex+3);
        location.setVariant("t_current<="+t3,automatas.get(Auto_index).parameters);
        location.setFlow("theta1'=omega1 &amp; theta2'=omega2 &amp; theta3'=omega3 &amp; theta4'=omega4 &amp; theta5'=omega5 &amp;theta6'=omega6 &amp;"+tmp3,automatas.get(Auto_index).parameters);

    }
    void setForward(double[] time,double lastTime, int Auto_index,int locIndex, double[] theta_tar,double[] a_forward,double[] v_forward){
        double t1=time[0]+lastTime;
        double t2=time[1]+lastTime;
        double t3=time[2]+lastTime;
        String tmp="";
        for(int i=0;i<6;i++){
            if(Auto_index==0){
                tmp=tmp+"a1_theta"+Integer.toString(i+1)+"="+Double.toString(theta_tar[i])+" &amp;";
            }
            else{
                tmp=tmp+"a2_theta"+Integer.toString(i+1)+"="+Double.toString(theta_tar[i])+" &amp;";
            }
        }

        Transition transition1=automatas.get(Auto_index).getTransitionBySourceAndTarget(path.get(Auto_index).get(locIndex),path.get(Auto_index).get(locIndex+1));
        //todo forward mode is not the first mode
        transition1.setAssignment(tmp,automatas.get(Auto_index).parameters);

        String tmp1="";
        String tmp2="";
        String tmp3="";
        for(int i=0;i<3;i++){
            tmp1=tmp1+"x"+Integer.toString(i+1)+"'=v"+Integer.toString(i+1)+" &amp;";
            tmp1=tmp1+"v"+Integer.toString(i+1)+"'="+Double.toString(a_forward[i])+"&amp;";

            tmp2=tmp2+"x"+Integer.toString(i+1)+"'="+Double.toString(v_forward[i])+"&amp;";

            tmp3=tmp3+"x"+Integer.toString(i+1)+"'=v"+Integer.toString(i+1)+" &amp;";
            tmp3=tmp3+"v"+Integer.toString(i+1)+"'="+Double.toString(a_forward[i]*(-1))+"&amp;";

        }
        Location location=automatas.get(Auto_index).locations.get(locIndex+1);
        location.setVariant("t_current<="+t1,automatas.get(Auto_index).parameters);
        location.setFlow(tmp1,automatas.get(Auto_index).parameters);
        Transition transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+1,locIndex+2);
        transition.setGuard("t=="+t1,automatas.get(Auto_index).parameters);

        location=automatas.get(Auto_index).locations.get(locIndex+2);
        location.setVariant("t_current<="+t2,automatas.get(Auto_index).parameters);
        location.setFlow(tmp2,automatas.get(Auto_index).parameters);
        transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+2,locIndex+3);
        transition.setGuard("t=="+t2,automatas.get(Auto_index).parameters);

        location=automatas.get(Auto_index).locations.get(locIndex+3);
        location.setVariant("t_current<="+t3,automatas.get(Auto_index).parameters);
        location.setFlow(tmp3,automatas.get(Auto_index).parameters);
    }
    private void setDoorlike(double[] time, double lastTime, int Auto_index, int locIndex, double[] theta_tar, double[] v_doorlike,double v_max) {
        double t1=time[0]+lastTime;
        double t2=time[1]+lastTime;
        double t3=time[2]+lastTime;
        String tmp="";
        for(int i=0;i<6;i++){
            if(Auto_index==0){
                tmp=tmp+"a1_theta"+Integer.toString(i+1)+"="+Double.toString(theta_tar[i])+" &amp;";
            }
            else{
                tmp=tmp+"a2_theta"+Integer.toString(i+1)+"="+Double.toString(theta_tar[i])+" &amp;";
            }
        }
        Transition transition1=automatas.get(Auto_index).getTransitionBySourceAndTarget(path.get(Auto_index).get(locIndex),path.get(Auto_index).get(locIndex+1));
        //todo forward mode is not the first mode
        transition1.setAssignment(tmp,automatas.get(Auto_index).parameters);


        String tmp1="x3'="+Double.toString(v_max)+"&amp;";
        String tmp2="";
        String tmp3="x3'="+Double.toString(v_max*(-1))+"&amp;";

        for(int i=0;i<3;i++){
            tmp2=tmp2+"x"+Integer.toString(i+1)+"'="+Double.toString(v_doorlike[i])+"&amp;";
        }
        Location location=automatas.get(Auto_index).locations.get(locIndex+1);
        location.setVariant("t_current<="+t1,automatas.get(Auto_index).parameters);
        location.setFlow(tmp1,automatas.get(Auto_index).parameters);
        Transition transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+1,locIndex+2);
        transition.setGuard("t=="+t1,automatas.get(Auto_index).parameters);

        location=automatas.get(Auto_index).locations.get(locIndex+2);
        location.setVariant("t_current<="+t2,automatas.get(Auto_index).parameters);
        location.setFlow(tmp2,automatas.get(Auto_index).parameters);
        transition=automatas.get(Auto_index).getTransitionBySourceAndTarget(locIndex+2,locIndex+3);
        transition.setGuard("t=="+t2,automatas.get(Auto_index).parameters);

        location=automatas.get(Auto_index).locations.get(locIndex+3);
        location.setVariant("t_current<="+t3,automatas.get(Auto_index).parameters);
        location.setFlow(tmp3,automatas.get(Auto_index).parameters);
    }
    public int getstepnum(){return stepnum;}
    public double[] getsingleTime(){return singletimeProfile;}
    public ArrayList<Integer> getVaildRange(Instance pos){
        ArrayList<Integer> vaildRange=new ArrayList<>();
        //Two arms choose paths
        vaildRange.add(0);
        for(int n=0;n<autonum;n++){
            double choice=combin.get((int)pos.getFeature(0)).get(n);
            ArrayList<String> com = PathMap.get((int) choice);
            int index=1;
            for(int j=0;j<com.size()-1;j++){
                String s=com.get(j);
                switch (s){
                    case "fast":
                        for(int i=0 ;i<6;i++){
                            vaildRange.add(index+i);
                        }
                        index+=9;
                        break;
                    case "forward":
                        for(int i=0 ;i<7;i++){
                            vaildRange.add(index+i);
                        }
                        index+=9;
                        break;
                    case "doorlike":
                        for(int i=0 ;i<6;i++){
                            vaildRange.add(index+i);
                        }
                        vaildRange.add(index+7);
                        index+=9;
                        break;
                }
            }

        }
        return vaildRange;
    }

}
