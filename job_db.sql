SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;
SET default_tablespace = '';
SET default_with_oids = false;



SET search_path = "public", pg_catalog;

CREATE TABLE "job_tb" (
    "id" int4 NOT NULL,
    "depcode" varchar(80) NOT NULL,
    "depjob" varchar(400) NOT NULL,
    "description" varchar(1020) NOT NULL
);

ALTER TABLE "job_tb" OWNER TO "postgres";

DELETE FROM "job_tb";
INSERT INTO "job_tb"("id", "depcode", "depjob", "description") VALUES ('4', '2', 'job1', 'description3');
INSERT INTO "job_tb"("id", "depcode", "depjob", "description") VALUES ('5', '3', 'job2', 'description4');
INSERT INTO "job_tb"("id", "depcode", "depjob", "description") VALUES ('1', '1', 'job1', 'description2');
INSERT INTO "job_tb"("id", "depcode", "depjob", "description") VALUES ('6', '4', 'job5', 'description7');
INSERT INTO "job_tb"("id", "depcode", "depjob", "description") VALUES ('2', '1', 'job2', 'description6');


CREATE SEQUENCE "job_tb_id_seq";

ALTER TABLE "job_tb_id_seq" OWNER TO "postgres";

ALTER SEQUENCE "job_tb_id_seq" OWNED BY "job_tb"."id";

SELECT pg_catalog.setval('"job_tb_id_seq"', 6, false);


ALTER TABLE "job_tb" ALTER COLUMN "id"
SET DEFAULT nextval('job_tb_id_seq'::regclass);


ALTER TABLE ONLY "job_tb" ADD CONSTRAINT "job_tb_depcode_depjob_key"
UNIQUE (depcode, depjob);

ALTER TABLE ONLY "job_tb" ADD CONSTRAINT "job_tb_pkey"
PRIMARY KEY (id);
