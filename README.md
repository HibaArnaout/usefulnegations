# usefulnegations
Find out useful negations in your dataset.

# Overview
This code takes as input a tabular dataset (.tsv) containing information about entities (items) and produces an output file containing useful negations about the entities.

# Methodology
The method used to retrieve useful negations is the **peer-based statistical inference** method in: *Enriching Knowledge Bases with Negative Statements*, Arnaout et al., AKBC 2020  (full paper [1] and video-summary [2]). The method is explained in detail in the paper.

In brief: Given a structured-information store (in this case a tabular dataset), we want to discover **useful negative statements** about entities in the dataset. A useful negation is a negative statement about an entity that is *noteworthy*. Two examples are "**Stephen Hawking did not win the Nobel Pize in Physics**" and "**Switzerland is not a member of the EU**". We discover these useful negations using what we call the *peer-based statistical inference method*. We select *highly* related entities (**peers**) to an entity e, that set expectations about e, where the negation of these expectations are potentially salient. Say, the dataset is about scientists, and the column titles (features) are: name, occupation(s), citizenship(s), gender, academic degree(s), award(s), etc... Then one reasonable feature to use for peering is occupation, where every winner's peers are other winners having a similar occupation(s). A local closed-world assumption is made within a group of peers. The peer groups are used to discover candidate negative statements that can be overwhelming sometimes, and not all useful. For example, 70% of peers of Stephen Hawking won the Nobel Prize in Physics, but only 2% are Indian citizens. Then the negative statement "not winning a Nobel Prize in Physics" is more salient to Stephen Hawking than "not Indian citizen". For this reason, we proceed to rank the inferred candidate negative statements using relative frequency (what portion of the entity's peers have a certain statement). Finally, we output the top-k negations for every entity in the dataset.

# Input datasets
We provid three sample input datasets*, but you can use your own. One example is the Turing Award winners dataset:

| name | occupation | gender | citizen | employer | member of | academic degree | religion | residence | ethnicity | field | education | 
| ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- | ------------- |
| Donald Knuth | computer scientist,academic,programmer,historian of mathematics,writer,university teacher,mathematician,engineer | male | United States of America | California Institute of Technology,Stanford University,Burroughs Corporation,Institute for Defense Analyses | National Academy of Engineering,National Academy of Sciences,Association for Computing Machinery,London Mathematical Society,Royal Society,Institute of Electrical and Electronics Engineers,French Academy of Sciences,Russian Academy of Sciences,Norwegian Academy of Science and Letters,American Academy of Arts and Sciences,American Philosophical Society,American Mathematical Society,Bavarian Academy of Sciences and Humanities,British Computer Society | Doctor of Philosophy | Lutheranism |  |  | analysis of algorithms,computer science,combinatorics | California Institute of Technology,Milwaukee Lutheran High School,Case Western Reserve University | 
| Shafrira Goldwasser | cryptographer,computer scientist,university teacher,mathematician,engineer | female | United States of America,Israel | Massachusetts Institute of Technology,Weizmann Institute of Science | National Academy of Engineering,National Academy of Sciences,American Academy of Arts and Sciences,Israel Academy of Sciences and Humanities |  |  | Israel |  | computer science | Carnegie Mellon University,University of California  Berkeley | 
| Tim Berners-Lee | computer scientist,programmer,web developer,inventor,university teacher,engineer,physicist | male | United Kingdom | School of Electronics and Computer Science  University of Southampton,CERN,Massachusetts Institute of Technology,World Wide Web Consortium,Plessey,Open Data Institute | National Academy of Engineering,National Academy of Sciences,American Academy of Arts and Sciences,Association for Computing Machinery,Royal Society | professor,Bachelor of Arts | Unitarian Universalism | Concord | English people | information technology,computer science | The Queen's College,Emanuel School | 
| Stephen Cook | computer scientist,university teacher,mathematician | male | Canada,United States of America | University of California  Berkeley,University of Toronto | National Academy of Sciences,American Academy of Arts and Sciences,Association for Computing Machinery,Royal Society of Canada,Royal Society,Göttingen Academy of Sciences | Doctor of Sciences |  |  |  | computer science | University of Michigan,Harvard University | 

For entities with multiple values for the same feature (multiple occupations and awards), the information can be concatenated in one comma-serparated value. Please check the full dataset, these can be found in "usefulnegations/v.1/".

# Sources

1) turing_award_winners.tsv; source: [https://www.wikidata.org](https://www.wikidata.org),
2) usa_presidents.tsv; source: [https://www.wikidata.org](https://www.wikidata.org),
3) india_hotels.tsv; source: [https://www.booking.com](https://www.booking.com).

# Code Parameters
To run the main in "usefulnegations/v.1/src/code.java", you need *three* parameters:

1) input file (.tsv), you can check input examples in "usefulnegations/v.1/".
2) peering column (integer), speficifying the column you want the program to use for peering entities. For example, peering column = 2 is gender and = 3 is citizen in the above example.. which means, peering by entities sharing the same gender in the first case and the same citizenship in the second case. If you don't want to do peering at all (consider all entities in the dataset as peers), set this parameter to -1. For entities with less than 3 peers, the code falls back on the -1 option, as 1 and 2 peers are not enough.
3) k (integer), how many useful negations for every entity (top-k).
Column 0 is always assumed to be the entities column (people names, hotel names, product ids, etc...)

# Output
After running the code on your input, check "usefulnegations/v.1/output/".

You will find your output file under the name "negations_[peering_col]_[k]_[inputfilename].tsv" For example, for the Turing Award winners, with gender for peering, and top-5 results for every entity, the output file name is "negations_2_5_turing_winners.tsv".

Some examples of results from this file: Format: entity [position: ¬(statement)=relative frequency...]
**Tim Berners-Lee** [1: ¬*(citizen; United States of America)*=0.72 2: ¬*(occupation; mathematician)*=0.47 3: ¬*(employer; Stanford University)*=0.19 4: ¬*(field; informatics)*=0.18 5: ¬*(academic degree; Doctor of Philosophy)*=0.16]
This can be interpreted as: Berners-Lee is not a citizen of the United States, unlike 72% of **male** Turing Award winners. Also, Berners-Lee is/was not employed by Stanford University, unlike 19% of **male** Turing Award winners.

# Feedback
If you found this code useful we would be happy to hear from you. For any suggestions, bugs, or general feedback, you can contact us on:
**E-mail**: harnaout@mpi-inf.mpg.de. **Twitter**: @negationInKBs.

And check our [webpage](https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/knowledge-base-recall/interesting-negation-in-kbs) on useful negations in knowledge bases.

# References
[1] [Enriching Knowledge Bases with Interesting Negative Statements, Arnaout et al., AKBC 2020](https://www.akbc.ws/2020/assets/pdfs/pSLmyZKaS.pdf)

[2] [5-min video presentation summarizing the paper [1]](https://www.youtube.com/watch?v=Q-C2MbzGXjc)
