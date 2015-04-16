<h1>Undo/Redo Logging</h1>
<hr/>
<p>
Cory Hayward, He Xuebin, Thinh Nguyen <br/>
Project 4 for CS542: Database Management Systems <br/>
Professor Singh <br/>
15 April 2015
</p>
<h3> Introduction </h3>
This project aimed to build an undo/redo logging mechanism by implementing the following requirements.
<blockquote>
"
 Over time, the populations of cities and countries changes. We will change the population of each by 2% to represent the passage of a year. The purpose of this programming assignment is to programmatically make this change in all records and to generate undo/redo logs as we are doing it. Then we will move those logs to another machine that has a copy of the same data and apply the logs and observe that the data has changed.
"
</blockquote>

<h3> Database Type </h3>
<p>
	We are going to reuse the city.csv and country.csv file from previous project as our databases.
</p>
<h3> Update Type</h3>
<p>
	To represent the change in population of city, we specified two types of change: INCREASE_BY_PERCENTAGE and DECREASE_BY_PERCENTAGE.
</p>
<h3> Record </h3>
<p>
	The record model or class is a way that we abstractly represent data. It has a title row and multiple data rows.
</p>
<h3> Relation </h3>
<p>
	Relation class represents a relation by holding a pointer to the relation csv file. This class has two crucial methods those are updateAll() and redo()
</p>
<p>
	<b>void updateAll(UpdateType type, String columnName, int coefficient)</b>: this method reads in the csv file, identifies the update type and column, then increase/decrease the value at the specified column by coefficient value. For instance <br />
	<b>updateAll(UpdateType.INCREASE_BY_PERCENTAGE, "Population", 2);</b> means increase the population column by 2 percent. <br />
	Besides, the method also create a .csv.log file which represents the change. The order of data in that log file is as follow <br />
	Id, Column, Old Value, New Value
	
</p>
<p>
	<b>void redo()</b>: This method is used to redo the change from a log. If we use this method on another machine that has the same data (along with the log file), we can redo the change.
</p>
<h3> How did we test it</h3>
<b> updateAll() method test</b>
<p>
	To test update all, we used this method in java main method. We created a Relation object with a corresponding csv file and called updateAll() to increase the population column by 2 percent. <br />
	Relation relation = new Relation("city.csv"); <br />
	relation.updateAll(UpdateType.INCREASE_BY_PERCENTAGE, "Population", 2); <br/>
	<b> Result </b> <br />
	We observed that the population column changed by 2 percent.
	
</p>
<b> redo()</b>
<p>
	To test the redo method, we used the generated log file from updateAll(), then run the redo method for the original data. <br />
	Relation relation = new Relation("city.csv"); <br />
	relation.redo() <br />
	<b>Result </b> <br />
	We succesfully redo the csv file using redo log.
</p>
<h3> Assumption </h3>
<ul>
	<li> In order to update the csv file, we has to write the update data into a tempfile, delete the orginal file and rename the temp file. We made an assumption that the temp file will be "tmp_"+path, which currently only work if the csv files are in the default folder of eclipse </li>
	<li>
		Similarly, we generated the log file in the default folder of eclipse
	</li>
</ul>

