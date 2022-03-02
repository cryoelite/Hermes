-- ************************************** `message`

CREATE TABLE `message`
(
 `messageID`        varchar(64) NOT NULL ,
 `sender`           varchar(64) NOT NULL ,
 `recipient`        varchar(64) NOT NULL ,
 `messageContentID` varchar(128) NOT NULL ,
 `timestamp`        timestamp NOT NULL ,
 `read`             bit NOT NULL ,
 `delivered`        bit NOT NULL ,

PRIMARY KEY (`messageID`),
KEY `FK_46` (`messageContentID`),
CONSTRAINT `FK_44` FOREIGN KEY `FK_46` (`messageContentID`) REFERENCES `messageData` (`messageContentID`),
KEY `FK_58` (`recipient`),
CONSTRAINT `FK_56` FOREIGN KEY `FK_58` (`recipient`) REFERENCES `user` (`userID`),
KEY `FK_61` (`sender`),
CONSTRAINT `FK_59` FOREIGN KEY `FK_61` (`sender`) REFERENCES `user` (`userID`)
);

