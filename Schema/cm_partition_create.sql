CREATE TABLE cm_partition
(
  partition_name VARCHAR(64)  NOT NULL,
  table_name     VARCHAR(200) NOT NULL,
  partition_unit CHAR         NOT NULL,
  data_term      INT          NOT NULL,
  index_column   VARCHAR(64)  NOT NULL,
  description    VARCHAR(64)  NOT NULL,
  CONSTRAINT `PRIMARY`
  PRIMARY KEY (table_name)
);

-- insert sample
-- INSERT INTO cm_partition (partition_name, table_name, partition_unit, data_term, index_column, description) VALUES ('P', 'table_name', 'D', 180, 'partition_column', 'table description');