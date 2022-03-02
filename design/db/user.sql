CREATE TABLE `user`
(
 `userID` varchar(64) NOT NULL ,
 `name`   nvarchar(50) NULL ,
 `iconID` varchar(64) NOT NULL ,
 `email`  varchar(50) NOT NULL ,

PRIMARY KEY (`userID`),
KEY `FK_55` (`iconID`),
CONSTRAINT `FK_53` FOREIGN KEY `FK_55` (`iconID`) REFERENCES `userIcon` (`iconID`)
);
