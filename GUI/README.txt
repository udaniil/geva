#README

#GEVA-1.1

#System requirements
#- Java 1.5

#Run
java -jar GEVA

#Parameters
#- Parameters can be entered via the command line
#"java -jar GEVA-1-beta.jar -h" to see the parameters or look in
#Parameters/HelloWorld.properties for a better description

#Examples
#- Run default word match
java -jar GEVA-1-beta.jar
#- Too run the SantaFeAntTrail fitness function 
java -jar GEVA-1-beta.jar -fitness_function FitnessEvaluation.SantaFeAntTrail.SantaFeAntTrailBSF -grammar_file Grammars/sf_grammar_gr.bnf
#- Too run the EvenFiveParity fitness function
java -jar GEVA-1-beta.jar -fitness_function FitnessEvaluation.ParityProblem.EvenFiveParityFitnessBSF -grammar_file Grammars/efp_grammar_gr.bnf -output Desktop/tmp/HelloWorld/
#- Too run the SymbolicRegression fitness function
java -jar GEVA-1-beta.jar -fitness_function FitnessEvaluation.SymbolicRegression.SymbolicRegressionJScheme -grammar_file Grammars/simple_sr.bnf
#- Too change the mutation probability to 0.1%
java -jar GEVA-1-beta.jar -mutation_probability 0.001
#- Too increase wrapping
java -jar GEVA-1-beta.jar -max_wraps 2
#-Too run with steady state replacement
java -jar GEVA-1-beta.jar -replacement_type steady_state
#-To run with properties file
java -jar GEVA-1-beta.jar -properties_file Parameters/HelloWorld.properties
#- Too change maximum memory to 512 MB and use the server virtual machine
java -server -Xmx512m -jar GEVA-1-beta.jar
