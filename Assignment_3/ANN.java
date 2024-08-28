// Dominqiue da Silva

import java.util.Random;

public class ANN {

    private int inputSize; //number of input size or features in the data set
    private int hiddenSize; //number of neurons in the hidden layer
    // user defined value
    // increasing the size can improve the network's capacity to learn but computational cost
    // private int outputSize; // number of output neurons 
    // we are using an activation function and doing classification with 2 classes
    // therefore the size would be set to 1
    private double[][] hiddenWeights; // weights between the input layer and the hidden layer
    private double[] hiddenBiases; // bias values for each neuron in the hidden layer 
    private double[] outputWeights;  // weights between the hidden layer and the output layer
    private double outputBias; // bias value for the output neuron 
    private double learningRate; // rate at which the weights are updated during the learning process
    // controls the magnitude of weight adjustments in each iteration of the backpropogation 
    // needs careful selection such that to ensure that the network converges to an optimal solution without overshooting or getting stuck in a local optima
    private Random random = new Random(1234); // setting seed for randomization 

    ////////////////////////////////////////////////////////////////////////////////////////
    public ANN(int[][] instances){
        inputSize = 9;
        hiddenSize = 10; // Adjust the number of neurons in the hidden layer as needed
        // outputSize = 1;
        learningRate = 0.12; // Adjust the learning rate as needed
        hiddenWeights = new double[hiddenSize][inputSize];
        hiddenBiases = new double[hiddenSize];
        outputWeights = new double[hiddenSize];
        outputBias = 0;
        // Initialize weights and biases with random values
        initializeWeightsAndBiases();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void initializeWeightsAndBiases() {
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                hiddenWeights[i][j] = random.nextDouble() - 0.5; // Initialize weights between -0.5 and 0.5
            }
            hiddenBiases[i] = random.nextDouble() - 0.5; // Initialize biases between -0.5 and 0.5
            outputWeights[i] = random.nextDouble() - 0.5;
        }
        outputBias = random.nextDouble() - 0.5;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private double[] calculateHiddenLayerOutputs(int[] inputs) {
        // foward propogation step for the hidden layer
        // takes the input and applies the weights and biases to calculate the outputs of the hidden layer neurons
        // it uses the ReLU actication function 
        double[] hiddenLayerOutputs = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) { //loops over every neuron in the hidden layer 
            double sum = 0; // weight sum of it's inputs
            for (int j = 0; j < inputSize; j++) {
                sum += hiddenWeights[i][j] * inputs[j];
            }
            hiddenLayerOutputs[i] = relu(sum + hiddenBiases[i]); // activation function is applied
        }
        return hiddenLayerOutputs; // array containing the output values of the hidden layer neurons
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private double calculateOutput(double[] hiddenLayerOutputs) {
        // forward propogation for the output layer of the neural network
        // outputs of the hidden layer, applies weights and biases and calculates the output layer nerons
        // we are using the sigmoid activation function
        double sum = 0;
        for (int i = 0; i < hiddenSize; i++) { // looping through hidden layer neurons
            sum += outputWeights[i] * hiddenLayerOutputs[i];
        }
        return sigmoid(sum + outputBias);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void updateOutputWeights(double[] hiddenLayerOutputs, double loss) {
        for (int i = 0; i < hiddenSize; i++) {
            double gradient = loss * sigmoidDerivative(hiddenLayerOutputs[i]);
            outputWeights[i] += learningRate * gradient;
        }

        outputBias += learningRate * loss;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void updateHiddenWeights(int[] inputs, double[] hiddenLayerOutputs, double loss) {
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                double gradient = loss * sigmoidDerivative(hiddenLayerOutputs[i]) * inputs[j];
                hiddenWeights[i][j] -= learningRate * gradient;
            }

            double gradient = loss * sigmoidDerivative(hiddenLayerOutputs[i]);
            hiddenBiases[i] += learningRate * gradient;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private double relu(double x) {
        return Math.max(0, x);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private double sigmoidDerivative(double x) {
        double sigmoidX = sigmoid(x);
        return sigmoidX * (1 - sigmoidX);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    public void train(int[][] instances, int[] labels, int epochs) {
        for (int epoch = 1; epoch <= epochs; epoch++) {
            double totalError = 0;
            for (int i = 0; i < instances.length; i++) {
                int[] inputs = instances[i];
                int label = labels[i];

                double[] hiddenLayerOutputs = calculateHiddenLayerOutputs(inputs);
                double output = calculateOutput(hiddenLayerOutputs);

                double error = label - output;
                totalError += Math.abs(error);

                updateOutputWeights(hiddenLayerOutputs, error);
                updateHiddenWeights(inputs, hiddenLayerOutputs, error);
            }

            double averageError = totalError / instances.length;
            System.out.println("Epoch: " + epoch + ", Average Error: " + averageError);
        }
    }




    
    ////////////////////////////////////////////////////////////////////////////////////////
    public int predict(int[] inputs) {
        double[] hiddenLayerOutputs = calculateHiddenLayerOutputs(inputs);
        double output = calculateOutput(hiddenLayerOutputs);
        return output >= 0.5 ? 1 : 0;
        //return output >= 0.5 ? 0 : 1;
    }

}