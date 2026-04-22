/* L�gico_2: */

CREATE TABLE Users (
    cduser int PRIMARY KEY,
    dtbirth date,
    fgdocument int,
    document varchar(20),
    nmuser varchar(100),
    nrcellphone varchar(15),
    cdaddress int,
    cdoccupation int
);

CREATE TABLE Addresses (
    cdaddress int PRIMARY KEY,
    cdzipcode char(8),
    nmstreet varchar(100),
    nraddress varchar(10),
    dscomplement varchar(50),
    cddistrict int
);

CREATE TABLE Countries (
    cdcountry int PRIMARY KEY,
    nmcountry varchar(60),
    sgcountry char(3)
);

CREATE TABLE States (
    cdstate int PRIMARY KEY,
    nmstate varchar(50),
    sgstate char(2),
    cdcountry int
);

CREATE TABLE Cities (
    cdcity int PRIMARY KEY,
    nmcity varchar(60),
    cdstate int
);

CREATE TABLE Districts (
    cddistrict int PRIMARY KEY,
    nmdistrict varchar(60),
    cdcity int
);

CREATE TABLE Roles (
    cdrole int PRIMARY KEY,
    nmrole varchar(50)
);

CREATE TABLE User_Contract (
    cdcontract int,
    cduser int,
    cdrole int
);

CREATE TABLE Contracts (
    cdcontract int PRIMARY KEY,
    dtcreation date,
    dstitle varchar(100),
    cdtemplate int,
    cdproperty int,
    cdindex int
);

CREATE TABLE Topics (
    cdtopic int PRIMARY KEY,
    nmtopic varchar(100)
);

CREATE TABLE Clauses (
    cdclause int PRIMARY KEY,
    dstext text,
    cdtopic int
);

CREATE TABLE Variables (
    cdvariable int PRIMARY KEY,
    nmvariable varchar(50),
    vlvariable varchar(255),
    tpvariable int,
    fgtriggeralert boolean,
    cdcontract int
);

CREATE TABLE Broker_Data (
    nrcreci varchar(20)
);

CREATE TABLE Bank_Accounts (
    cdbankaccount int PRIMARY KEY,
    nragency varchar(30),
    nraccount varchar(30),
    nrpixkey varchar(100),
    cduser int
);

CREATE TABLE Properties (
    cdproperty int PRIMARY KEY,
    nrregistration varchar(30),
    dsdescription text,
    vltotalarea_ decimal(10,2),
    cdaddress int,
    cdtype int,
    cdpurpose int,
    cdstatus int
);

CREATE TABLE Commissions (
    cdcommission int PRIMARY KEY,
    vlcommission decimal(12,2),
    dtpayment date,
    cdcontract int
);

CREATE TABLE Property_Types (
    cdtype int PRIMARY KEY,
    nmtype varchar(30)
);

CREATE TABLE Property_Purposes (
    cdpurpose int PRIMARY KEY,
    nmpurpose varchar(20)
);

CREATE TABLE Property_Status (
    cdstatus int PRIMARY KEY,
    nmstatus varchar(30)
);

CREATE TABLE Occupations (
    cdoccupation int PRIMARY KEY,
    nmoccupation varchar(100)
);

CREATE TABLE Contract_Templates (
    cdtemplate int PRIMARY KEY,
    nmtemplate varchar(100),
    dsversion varchar(10),
    fgactive char(1)
);

CREATE TABLE Template_Topics (
    cdtopic int,
    cdtemplate int
);

CREATE TABLE Indexes (
    cdindex int PRIMARY KEY,
    nmindex varchar(50)
);

CREATE TABLE Index_Rates (
    cdrate int PRIMARY KEY,
    refmonth int,
    refyear int,
    vlrate decimal(10,2),
    cdindex int
);

CREATE TABLE Installments (
    cdinstallment int PRIMARY KEY,
    dtdue date,
    vlbase decimal(10,2),
    vladjusted decimal(10,2),
    cdstatus int,
    dtpayment date,
    nrinstallment int,
    cdcontract int
);

CREATE TABLE Notifications (
    cdnotification int PRIMARY KEY,
    dsmessage text,
    dtsend date,
    fgstatus int,
    tpnotification int,
    cdcontract int,
    cduser int
);

CREATE TABLE Properties_Users (
    cdproperty int,
    cduser int
);
 
ALTER TABLE Users ADD CONSTRAINT FK_Users_2
    FOREIGN KEY (cdaddress)
    REFERENCES Addresses (cdaddress)
    ON DELETE RESTRICT;
 
ALTER TABLE Users ADD CONSTRAINT FK_Users_3
    FOREIGN KEY (cdoccupation)
    REFERENCES Occupations (cdoccupation)
    ON DELETE CASCADE;
 
ALTER TABLE Addresses ADD CONSTRAINT FK_Addresses_2
    FOREIGN KEY (cddistrict)
    REFERENCES Districts (cddistrict)
    ON DELETE RESTRICT;
 
ALTER TABLE States ADD CONSTRAINT FK_States_2
    FOREIGN KEY (cdcountry)
    REFERENCES Countries (cdcountry)
    ON DELETE RESTRICT;
 
ALTER TABLE Cities ADD CONSTRAINT FK_Cities_2
    FOREIGN KEY (cdstate)
    REFERENCES States (cdstate)
    ON DELETE RESTRICT;
 
ALTER TABLE Districts ADD CONSTRAINT FK_Districts_2
    FOREIGN KEY (cdcity)
    REFERENCES Cities (cdcity)
    ON DELETE RESTRICT;
 
ALTER TABLE User_Contract ADD CONSTRAINT FK_User_Contract_1
    FOREIGN KEY (cdcontract)
    REFERENCES Contracts (cdcontract);
 
ALTER TABLE User_Contract ADD CONSTRAINT FK_User_Contract_2
    FOREIGN KEY (cduser)
    REFERENCES Users (cduser);
 
ALTER TABLE User_Contract ADD CONSTRAINT FK_User_Contract_3
    FOREIGN KEY (cdrole)
    REFERENCES Roles (cdrole);
 
ALTER TABLE Contracts ADD CONSTRAINT FK_Contracts_2
    FOREIGN KEY (cdtemplate)
    REFERENCES Contract_Templates (cdtemplate)
    ON DELETE CASCADE;
 
ALTER TABLE Contracts ADD CONSTRAINT FK_Contracts_3
    FOREIGN KEY (cdproperty)
    REFERENCES Properties (cdproperty)
    ON DELETE SET NULL;
 
ALTER TABLE Contracts ADD CONSTRAINT FK_Contracts_4
    FOREIGN KEY (cdindex)
    REFERENCES Indexes (cdindex)
    ON DELETE SET NULL;
 
ALTER TABLE Clauses ADD CONSTRAINT FK_Clauses_2
    FOREIGN KEY (cdtopic)
    REFERENCES Topics (cdtopic)
    ON DELETE RESTRICT;
 
ALTER TABLE Variables ADD CONSTRAINT FK_Variables_2
    FOREIGN KEY (cdcontract)
    REFERENCES Contracts (cdcontract)
    ON DELETE RESTRICT;
 
ALTER TABLE Bank_Accounts ADD CONSTRAINT FK_Bank_Accounts_2
    FOREIGN KEY (cduser)
    REFERENCES Users (cduser)
    ON DELETE RESTRICT;
 
ALTER TABLE Properties ADD CONSTRAINT FK_Properties_2
    FOREIGN KEY (cdaddress)
    REFERENCES Addresses (cdaddress)
    ON DELETE CASCADE;
 
ALTER TABLE Properties ADD CONSTRAINT FK_Properties_3
    FOREIGN KEY (cdtype)
    REFERENCES Property_Types (cdtype)
    ON DELETE CASCADE;
 
ALTER TABLE Properties ADD CONSTRAINT FK_Properties_4
    FOREIGN KEY (cdpurpose)
    REFERENCES Property_Purposes (cdpurpose)
    ON DELETE CASCADE;
 
ALTER TABLE Properties ADD CONSTRAINT FK_Properties_5
    FOREIGN KEY (cdstatus)
    REFERENCES Property_Status (cdstatus)
    ON DELETE CASCADE;
 
ALTER TABLE Commissions ADD CONSTRAINT FK_Commissions_2
    FOREIGN KEY (cdcontract)
    REFERENCES Contracts (cdcontract)
    ON DELETE RESTRICT;
 
ALTER TABLE Template_Topics ADD CONSTRAINT FK_Template_Topics_1
    FOREIGN KEY (cdtopic)
    REFERENCES Topics (cdtopic);
 
ALTER TABLE Template_Topics ADD CONSTRAINT FK_Template_Topics_2
    FOREIGN KEY (cdtemplate)
    REFERENCES Contract_Templates (cdtemplate);
 
ALTER TABLE Index_Rates ADD CONSTRAINT FK_Index_Rates_2
    FOREIGN KEY (cdindex)
    REFERENCES Indexes (cdindex)
    ON DELETE RESTRICT;
 
ALTER TABLE Installments ADD CONSTRAINT FK_Installments_2
    FOREIGN KEY (cdcontract)
    REFERENCES Contracts (cdcontract)
    ON DELETE RESTRICT;
 
ALTER TABLE Notifications ADD CONSTRAINT FK_Notifications_2
    FOREIGN KEY (cdcontract)
    REFERENCES Contracts (cdcontract)
    ON DELETE RESTRICT;
 
ALTER TABLE Notifications ADD CONSTRAINT FK_Notifications_3
    FOREIGN KEY (cduser)
    REFERENCES Users (cduser)
    ON DELETE RESTRICT;
 
ALTER TABLE Properties_Users ADD CONSTRAINT FK_Properties_Users_1
    FOREIGN KEY (cdproperty)
    REFERENCES Properties (cdproperty);
 
ALTER TABLE Properties_Users ADD CONSTRAINT FK_Properties_Users_2
    FOREIGN KEY (cduser)
    REFERENCES Users (cduser);