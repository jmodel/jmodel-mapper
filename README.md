# jmodel-mapper
An extensible model mapping framework, compose of an eclipse plugin (provide a simple model mapping DSL)  and a general model mapping core. 

# What is it?
Mapping between models in different formats always is boring. You have to face various technologies, languages, and frameworks. Why don't you imagine such a scene? I know the structures of source model and target model, then point out the mapping relation between fields, then again, say I want to convert the model from JSON to XML, it's done. JSON? XML? Should I care them? No. OK, jmodel-mapper can do this for you. This is what jmodel-mapper is.

# Why use it?
How long will it take to finish a mapping job? One day or one week? If you are not very familiar with JSON or XML, or other specified model format? jmodel-mapper could save your days and hours, because you don't need to learn more technologies, and DSL Editor can help you writing mapping rules quickly. 

# How to use it?
mvn building...<br />
install plugin...<br />
create java project (refer to the sample) and add new file named xxx.mapping...<br />
write your application (refer to the sample)...<br />
run and get your expected result...<br />



# Platform Support
JDK 8<br />
Eclipse Neon<br />

# Features
only support json (xml has not been tested now)<br />
basic mapping language support<br />

# TODO
many things...<br />

# License
MIT
