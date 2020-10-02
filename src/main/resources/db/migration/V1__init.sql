CREATE TABLE IF NOT EXISTS excerpt(
  excerptID INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  author VARCHAR(45) NOT NULL, 
  title VARCHAR(255) NOT NULL, 
  text VARCHAR(2500) NOT NULL,
  comments VARCHAR(2500) NOT NULL,
  PRIMARY KEY (excerptID)
) ENGINE=INNODB CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS tag(
  tagID INT UNSIGNED NOT NULL AUTO_INCREMENT,
  description VARCHAR(255) NOT NULL , 
  PRIMARY KEY (tagID)
) ENGINE=INNODB CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS tagmap (
  excerptID INT UNSIGNED NOT NULL, 
  tagID INT UNSIGNED NOT NULL, 
  PRIMARY KEY (excerptID, tagID),
    CONSTRAINT excerptFK FOREIGN KEY (excerptID) REFERENCES excerpt (excerptID)
    ON DELETE CASCADE 
    ON UPDATE CASCADE,
    CONSTRAINT tagFK FOREIGN KEY (tagID) REFERENCES tag (tagID)
    ON DELETE CASCADE 
    ON UPDATE CASCADE
) ENGINE=INNODB;
