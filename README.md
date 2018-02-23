# Histogram_NER_Hadoop

This project is a Hadoop implementation in Java of the creation of Histograms relative to named entities occurrences in tweets. For NER (Name Entity Recognition) has been used [LingPipe](http://alias-i.com/lingpipe/demos/tutorial/read-me.html) library.

To run this project is necessary to give the right arguments:

**args[0] = input** --> it is a directory that must contain tweets in .xml format. It is necessary that the tweet texts are collocated in the xml tag <TweetText>. In the [input](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/input) directory it can be found a set of .xml files which are the dataset downloaded from [TwitterNEED](https://github.com/badiehm/TwitterNEED).

**args[1] = tweets** --> it is an empty directory where the program will save new .xml files containing only <TweetText> tag extracted from  the .xml files in input directory. In the [tweets](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/tweets) directory it can be found a set of .xml files which consisting in the extraction of the <TweetText> tag in .xml files in the [input](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/input) directory.

**args[2] = model** --> it is a directory containing one of the .AbstractCharLmRescoringChunker [models](http://alias-i.com/lingpipe/web/models.html) for NER avaiable to the lingPipe library . The model in the [model](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/model) directory is able to recognize _PERSONS_, _LOCATIONS_ and _ORGANIZATIONS_.

**args[3] = output** --> it is a directory that will be created from the program after its execution. In _parallel18computing/Histogram_NER_Hadoop/output_ there are the results of the program using [input](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/input) as input and with numRepetition set to two.

**args[4] = results** --> it is an empty directory where the program will save some files .txt using the result just obtained. In [results](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/results) directory there are the result files relative to the [input](https://github.com/parallel18computing/Histogram_NER_Hadoop/tree/master/input) input files. 

**args[5] = numRepetition** --> it is the number of repetition of the datasets in input file. This can be useful for test the program performance when the number of datasets increases, but do not add information about the most frequent named entities in tweets. To have more trusted results, it is necessary to add other different datasets of tweets.
