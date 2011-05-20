/*
 * Copyright 2011 Snoopy Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

Drop database If Exists snoopydb;

Create Database snoopydb Character Set utf8 Collate utf8_general_ci;

Use snoopydb;

Create table Module (
	idModule Int NOT NULL AUTO_INCREMENT,
	name Varchar(128),
 Primary Key (idModule)) ENGINE = InnoDB;

Create table Result (
	idResult Int NOT NULL AUTO_INCREMENT,
	idHost Int NOT NULL,
	idModule Int NOT NULL,
	datestamp Date,
	result Varchar(512),
 Primary Key (idResult)) ENGINE = InnoDB;

Create table Host (
	idHost Int NOT NULL AUTO_INCREMENT,
	idOs Int NOT NULL,
	name Varchar(128),
 Primary Key (idHost)) ENGINE = InnoDB;

Create table Os (
	idOs Int NOT NULL AUTO_INCREMENT,
	name Varchar(128),
 Primary Key (idOs)) ENGINE = InnoDB;


Alter table Result add Foreign Key (idModule) references Module (idModule) on delete cascade on update  restrict;
Alter table Result add Foreign Key (idHost) references Host (idHost) on delete cascade on update  restrict;
Alter table Host add Foreign Key (idOs) references Os (idOs) on delete  restrict on update  restrict;

Delimiter $$

Create Procedure storeResult(In osname Varchar(128), In hostname Varchar(128), In modulename Varchar(128), In result Varchar(512)) 
Begin
	Declare osIdCount Int;
	Declare hostIdCount Int;
	Declare moduleIdCount Int;

	Declare osId Int;
	Declare hostId Int;
	Declare moduleId Int;
	
	Select Count(*) Into osIdCount From Os Where name=osname;
	If osIdCount = 0 Then
		Insert Into Os(name) Values(osname);
		Set osId = LAST_INSERT_ID();	
	Else
		Select idOs Into osId From Os Where name=osname;
	End If;

	Select Count(*) Into hostIdCount From Host Where name=hostname;
	If hostIdCount = 0 Then
		Insert Into Host(idOs, name) Values(osId, hostname);
		Set hostId = LAST_INSERT_ID();	
	Else
		Select idHost Into hostId From Host Where name=hostname;
	End If;

	Select Count(*) Into moduleIdCount From Module Where name=modulename;
	If moduleIdCount = 0 Then
		Insert Into Module(name) Values(modulename);
		Set moduleId = LAST_INSERT_ID();	
	Else
		Select idModule Into moduleId From Module Where name=modulename;
	End If;

	Insert Into Result(idHost, idModule, datestamp, result) Values(hostId, moduleId, CURDATE(), result);
End;

$$

Delimiter ;


