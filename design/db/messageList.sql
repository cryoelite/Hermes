-- ************************************** `messageList`

CREATE TABLE `messageList`
(
 `messageID` varchar(64) NOT NULL ,

PRIMARY KEY (`messageID`),
KEY `FK_22` (`messageID`),
CONSTRAINT `FK_20` FOREIGN KEY `FK_22` (`messageID`) REFERENCES `message` (`messageID`)
);

