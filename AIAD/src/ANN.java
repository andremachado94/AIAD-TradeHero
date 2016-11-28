import java.util.*;

import static java.lang.Math.*;

/**
 * Created by andremachado on 10/10/16.
 */
public class ANN {

    private double[][] inputs; //TODO - estruturar os inputs recebidos
    private double[] expectedOutputs; //TODO - estruturar os outputs desejados
    private double[] realOutputs;

    private double alphaWeight[][];
    private double betaWeight[];

    private double hiddenProductSum[];
    private double outputProductSum;

    private int hiddenLayerSize;
    private int numberOfInputCells;

    private int numberOfInputs;
    private int numberOfOutputs;

    public ANN(ArrayList<Double> opens, ArrayList<Double> highs, ArrayList<Double> lows, ArrayList<Double> closes, ArrayList<Integer> volumes, ArrayList<Double>adjCloses){

      /*  System.out.println("CLOSE: "+close.size());

        inputs1 = new ArrayList<>(close.size());
        //inputs2 = new ArrayList<>(open.size());

        for(Double item : close) {
            double var = item.doubleValue();
            inputs1.add(var);
        }*/
/*
        for(Double item : open) {
            double var = item.doubleValue();
            inputs2.add(var);
        }
*/
        //Collections.copy(this.inputs1, inputs1);
        //Collections.copy(this.inputs2, inputs2);

        //TODO - Parse inputs
        //TODO - Get outputs

        //TODO
        //inputParser();

/*
        System.out.println("\n================================");
        System.out.println("      Setting test Values");
        System.out.println("================================\n");
        setTestValues();

        System.out.println("\n================================");
        System.out.println("Printing weights before training");
        System.out.println("================================\n");
        printWeights();

        System.out.println("\n================================");
        System.out.println("            Training");
        System.out.println("================================\n");
        trainNetwork();

        System.out.println("\n================================");
        System.out.println(" Printing weights after training");
        System.out.println("================================\n");
        printWeights();
*/

        hiddenLayerSize = 3;
        numberOfInputCells = 2;

        alphaWeight = new double[numberOfInputCells][hiddenLayerSize];
        betaWeight = new double[hiddenLayerSize];

        //setRandomWeights();
        setTestValues();
        //parseInputs(opens, highs, lows, closes, volumes, adjCloses);

        //hiddenProductSum = new double[hiddenLayerSize];
        //outputProductSum = new double[numberOfOutputLayers];

        //setExpectedOutputs();
        trainNetwork();
        //double res = parseResults();
        //System.out.println("Amanha fecha com subida / descida: " + res);

    }

    public void setTestValues(){

        //TEST-------

        alphaWeight[0][0]=0.8;
        alphaWeight[0][1]=0.4;
        alphaWeight[0][2]=0.3;
        alphaWeight[1][0]=0.2;
        alphaWeight[1][1]=0.9;
        alphaWeight[1][2]=0.5;

        betaWeight[0]=0.3;
        betaWeight[1]=0.5;
        betaWeight[2]=0.9;

        //------------

        hiddenProductSum = new double[hiddenLayerSize];

        expectedOutputs = new double[1];

        inputs = new double[2][1];

        inputs[0][0] = 1.0;
        inputs[1][0] = 1.0;
        expectedOutputs[0] = 0.0;
    }

    public void trainNetwork(){

        //setRandomWeights();

        //Calcula o produtório do Hidden Layer
        for (int i = 0; i < hiddenLayerSize; i++) {
            double productSum = 0;
            for (int j = 0; j < numberOfInputCells; j++) {
                productSum += (inputs[j][0] * alphaWeight[j][i]);
            }
            hiddenProductSum[i] = productSum;
            System.out.println();
        }


        //Calcula o produtório do Output
        double productSum = 0;
        for (int j = 0; j < hiddenLayerSize; j++) {
            productSum += (sigmoideFunction(hiddenProductSum[j]) * betaWeight[j]);
        }
        outputProductSum = productSum;


        //Calcula o erro em relação ao esperado
        double outputErrorMargin = expectedOutputs[0] - sigmoideFunction(outputProductSum);

        double deltaOutputSum = sigmoideDerivativeFunction(outputProductSum)*outputErrorMargin;


        System.out.println("RES: " + deltaOutputSum);



        System.out.println("\n=================================================================\n");
        //Back Propagation Time!


    }

    private void setRandomWeights(){
        for(int i = 0 ; i < hiddenLayerSize ; i++){
            for(int j = 0; j < numberOfInputCells; j++){
                alphaWeight[j][i] = randomNumber();
            }
                betaWeight[i] = randomNumber();

        }
    }

    public void printWeights(){
        //Print alpha
        System.out.println("Alpha Weights:\n");
        for(int i = 0 ; i < hiddenLayerSize ; i++) {
            for (int j = 0; j < numberOfInputCells; j++) {
                System.out.print(alphaWeight[j][i] + "\t");
            }
            System.out.println("\n");
        }

        //Print beta
        System.out.println("Beta Weights:\n");
        for(int i = 0 ; i < hiddenLayerSize ; i++) {
            System.out.print(betaWeight[i] + "\t");
        }
        System.out.println("\n");
    }

    public double randomNumber(){
        Random rn = new Random();
        return rn.nextDouble();
    }

    private double sigmoideFunction(double x){
        return 1/(1+exp(-x));
    }

    private double sigmoideDerivativeFunction(double x){
        return exp(x)/pow((exp(x)+1),2);
    }

/*
    private double sigmoideFunction(double x){
        return (1-exp(-2*x))/(1+exp(-2*x));
    }

    private double sigmoideDerivativeFunction(double x){
        return (exp(-2*x)*(4*exp(2*x) - 2*exp(4*x) + 2))/((exp(2*x)+1)*(exp(2*x)+1));
    }
*/
/*
    public int parseInputs(ArrayList<Double> opens, ArrayList<Double> highs, ArrayList<Double> lows, ArrayList<Double> closes, ArrayList<Integer> volumes, ArrayList<Double>adjCloses){

        numberOfInputs = opens.size();

        if(highs.size() != numberOfInputs || lows.size() != numberOfInputs || closes.size() != numberOfInputs || volumes.size() != numberOfInputs || adjCloses.size() != numberOfInputs){
            System.err.println("Non coherent number of inputs");
            return 1;
        }

        numberOfInputs--;
        numberOfOutputs=numberOfInputs-1;

        realOutputs = new double[6][numberOfOutputs];

        this.inputs = new double[12][numberOfInputs];

        for(int i = 0; i < numberOfInputs; i++){
            inputs[0][i] = opens.get(i+1)/opens.get(i) - 1;
            if(i>0) realOutputs[0][i-1] = inputs[0][i];
            if(inputs[0][i] < 0)    inputs[1][i]=1;
            else                    inputs[1][i]=0;
            inputs[0][i]=gaussNormalDestribution(inputs[0][i]);

            inputs[2][i] = highs.get(i+1)/highs.get(i) - 1;
            if(i>0) realOutputs[1][i-1] = inputs[2][i];
            if(inputs[2][i] < 0)    inputs[3][i]=1;
            else                    inputs[3][i]=0;
            inputs[2][i]=gaussNormalDestribution(inputs[2][i]);

            inputs[4][i] = lows.get(i+1)/lows.get(i) - 1;
            if(i>0) realOutputs[2][i-1] = inputs[4][i];
            if(inputs[4][i] < 0)    inputs[5][i]=1;
            else                    inputs[5][i]=0;
            inputs[4][i]=gaussNormalDestribution(inputs[4][i]);

            inputs[6][i] = closes.get(i+1)/closes.get(i) - 1;
            if(i>0) realOutputs[3][i-1] = inputs[6][i];
            if(inputs[6][i] < 0)    inputs[7][i]=1;
            else                    inputs[7][i]=0;
            inputs[6][i]=gaussNormalDestribution(inputs[6][i]);

            inputs[8][i] = ((double)volumes.get(i+1))/volumes.get(i) - 1;
            if(i>0) realOutputs[4][i-1] = inputs[8][i];
            if(inputs[8][i] < 0)    inputs[9][i]=1;
            else                    inputs[9][i]=0;
            inputs[8][i]=gaussNormalDestribution(inputs[8][i]);

            inputs[10][i] = opens.get(i+1)/adjCloses.get(i) - 1;
            if(i>0) realOutputs[5][i-1] = inputs[10][i];
            if(inputs[10][i] < 0)    inputs[11][i]=1;
            else                    inputs[11][i]=0;
            inputs[10][i]=gaussNormalDestribution(inputs[10][i]);

        }

        for(int i = 0; i < numberOfInputs; i++){
            for (int j=0 ; j < 12 ; j++){
                System.out.printf("%.7f  ", inputs[j][i]);
            }
            System.out.println();
        }

        return 0;

    }
*/
/*
    private void setExpectedOutputs(){
        expectedOutputs = new double[47][numberOfOutputs];

        for(int j=0 ; j < numberOfOutputs ; j++) {
            for (int i = 0; i < 4; i++) {
                expectedOutputs[nthDecimal(realOutputs[3][j],i+1) + 10*i][j] = 1.0;
            }

            if(realOutputs[3][j] < 0) expectedOutputs[40][j] = 1.0;
            if(abs(realOutputs[3][j]) >= 1.0) expectedOutputs[41][j] = 1.0;

            expectedOutputs[42][j] = realOutputs[0][j];
            expectedOutputs[43][j] = realOutputs[1][j];
            expectedOutputs[44][j] = realOutputs[2][j];
            expectedOutputs[45][j] = realOutputs[4][j];
            expectedOutputs[46][j] = realOutputs[5][j];
        }

        for(int i = 0; i < numberOfOutputs; i++){
            for (int j=0 ; j < 47 ; j++){
                System.out.printf("%d  ", (int)expectedOutputs[j][i]);
            }
            System.out.println();
        }

    }

    private double parseResults(){
        double sum=0;
        double productSum=0;
        for(int i = 0 ; i < 10 ; i++){
            sum += outputProductSum[i];
        }
        for(int i = 0 ; i < 10 ; i++){
            productSum += (i/10.0)*(outputProductSum[i]/sum);
        }

        sum = 0;
        for(int i = 10 ; i < 20 ; i++){
            sum += outputProductSum[i];
        }
        for(int i = 10 ; i < 20 ; i++){
            productSum += ((i-10)/100.0)*(outputProductSum[i]/sum);
        }

        sum = 0;
        for(int i = 20 ; i < 30 ; i++){
            sum += outputProductSum[i];
        }
        for(int i = 20 ; i < 30 ; i++){
            productSum += ((i-20)/1000.0)*(outputProductSum[i]/sum);
        }

        sum = 0;
        for(int i = 30 ; i < 40 ; i++){
            sum += outputProductSum[i];
        }
        for(int i = 30 ; i < 40 ; i++){
            productSum += ((i-30)/10000.0)*(outputProductSum[i]/sum);
        }

        if(outputProductSum[41] > 0.5) productSum+=1;
        if(outputProductSum[40] > 0.5) productSum*=-1;

        return productSum;
    }
*/
    private double gaussNormalDestribution(double x){
        return exp(-0.5*x*x);
    }

    private double gaussNormalDestribution(double x, double c){
        return exp(-0.5*x*x)/c;
    }

    private double inverseGaussNormalDestribution(double x){
        return sqrt(-2*log(x));
    }

    private int nthDecimal(double num , int n){
        return (int)(abs(num) * pow(10,n)) % 10;
    }

}