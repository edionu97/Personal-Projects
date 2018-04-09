# Student-Lab-Management

This application is used to manage the student's laboratories in the first semester( week 1 to 14 ).
It's a **desktop**,**java** application which uses a **SQL-Server** database.
This application when a new homework is added,updated or when a student recives a mark at one homework,sends an email through gmail server.
Has a **XML** configuration file,in which the repository type can be specified.The default repository type is SQLDatabaseRepository,but it can be changed to a file-repository,or to a mock repository(uses reflection for dynamically class loading)
For parsing the XML file,**DOM-parser** is used.
Also the statistics are exportend into **PDF**,using **ITEXT**,and for not slowing down the application this task + (**email sending**) is runned on another process(thread).

The **HTML** mail format is this:

<html>
    <head>
        <style>
             hh1{
                font-size:200%;
                font-style:italic;
                text-align:center;
             }
             p{
               font-size:150%;
               font-style:italic;
             }
             hh2{
                text-decoration:underline;
                text-align:left;
                font-size:150%;
             }
        </style>
    </head>
    <body>
        <hh1>Student information</h1>
        <p><strong>?</strong>,this is an informative message,showing information about your lab marks,and lab updates.</p>
        <hh2> Message content </h2>
        <p align="center" style="font-size:150%;font-style:italic;">?</p>
    </body>
</html>


Question marks are used in the application for giving relevant information to each student.

## Creating database

### Creating functions
 This functions are used in data pagination:

 CREATE FUNCTION [dbo].[paginateNota](@pageNumber INT,@pageElements INT) RETURNS @table TABLE (

		idStudent INT,idTema INT,observations VARCHAR (500),nota FLOAT

	) AS BEGIN

	DECLARE @start INT,@end INT;

	SET @start = (@pageNumber - 1) * @pageElements + 1;
	SET @end = @pageNumber * @pageElements;

	INSERT @table  SELECT a.idStudent,a.idTema,a.observations,a.nota FROM  (
		SELECT *,ROW_NUMBER() OVER (ORDER BY idStudent,idTema ASC) AS rowNum FROM Nota
	) a WHERE a.rowNum >= @start AND a.rowNum <= @end

	RETURN;
 END


 CREATE FUNCTION [dbo].[paginateStudents](@pageNumber INT,@pageElements INT) RETURNS @table TABLE (

		idStudent INT,nume VARCHAR(500),email VARCHAR(500),cadruDidactic VARCHAR(500),grupa INT

	) AS BEGIN

	DECLARE @start INT,@end INT;

	SET @start = (@pageNumber - 1) * @pageElements + 1;
	SET @end = @pageNumber * @pageElements;

	INSERT @table  select a.idStudent,a.nume,a.email,a.cadruDidactic,a.grupa  FROM (
		SELECT *,ROW_NUMBER() OVER (ORDER BY idStudent ASC) AS rowNum FROM Student
	) a WHERE a.rowNum >= @start AND a.rowNum <= @end

	RETURN;
 END

 CREATE FUNCTION [dbo].[paginateStudents](@pageNumber INT,@pageElements INT) RETURNS @table TABLE (

		idStudent INT,nume VARCHAR(500),email VARCHAR(500),cadruDidactic VARCHAR(500),grupa INT

	) AS BEGIN

	DECLARE @start INT,@end INT;

	SET @start = (@pageNumber - 1) * @pageElements + 1;
	SET @end = @pageNumber * @pageElements;

	INSERT @table  SELECT a.idStudent,a.nume,a.email,a.cadruDidactic,a.grupa  FROM (
		SELECT *,ROW_NUMBER() OVER (ORDER BY idStudent ASC)  rowNum FROM  Student
	) a WHERE a.rowNum >= @start AND a.rowNum <= @end

	RETURN;
  
 END

### Creating tables

 CREATE TABLE Tema(
   idTema INT PRIMARY KEY,
   deadline INT NOT NULL,
   cerinta VARCHAR(500)
 )


 CREATE TABLE Student(
    idStudent INT PRIMARY KEY,
    nume VARCHAR(500),
    email VARCHAR(500),
    cadruDidactic VARCHAR(500),
    grupa INT
 )


 CREATE TABLE Nota(
   idStudent INT REFERENCES Student(idStudent),
   idTema INT REFERENCES Tema(idTema),
   PRIMARY KEY(idStudent,idTema),
   observations VARCHAR(500),
   nota FLOAT
 )






