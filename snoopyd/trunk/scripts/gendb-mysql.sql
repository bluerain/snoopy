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


Create table Module (
	idModule Int NOT NULL AUTO_INCREMENT,
	name Varchar(128),
 Primary Key (idModule)) ENGINE = InnoDB;

Create table Result (
	idResult Int NOT NULL AUTO_INCREMENT,
	idHost Int NOT NULL,
	idModule Int NOT NULL,
	Datestamp Date,
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


