--
-- PostgreSQL database dump
--

\restrict L9ftLX4gdSxUYlk501tN5uXUgaI2y76wHxCF2C63tzE7Mag4n66Hecp2WtkyRUB

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: addresses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.addresses (
    cdaddress integer NOT NULL,
    cdzipcode character(8),
    nmstreet character varying(100),
    nraddress character varying(10),
    dscomplement character varying(50),
    cddistrict integer
);


--
-- Name: addresses_cdaddress_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.addresses ALTER COLUMN cdaddress ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.addresses_cdaddress_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: bank_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bank_accounts (
    cdbankaccount integer NOT NULL,
    nragency character varying(30),
    nraccount character varying(30),
    nrpixkey character varying(100),
    cduser integer
);


--
-- Name: bank_accounts_cdbankaccount_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.bank_accounts ALTER COLUMN cdbankaccount ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.bank_accounts_cdbankaccount_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cities (
    cdcity integer NOT NULL,
    nmcity character varying(60),
    cdstate integer
);


--
-- Name: cities_cdcity_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.cities ALTER COLUMN cdcity ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.cities_cdcity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: clauses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clauses (
    cdclause integer NOT NULL,
    dstext text,
    cdtopic integer,
    nrorder integer
);


--
-- Name: clauses_cdclause_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.clauses ALTER COLUMN cdclause ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.clauses_cdclause_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: contract_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contract_templates (
    cdtemplate integer NOT NULL,
    nmtemplate character varying(100),
    dsversion character varying(10),
    fgactive boolean
);


--
-- Name: contract_templates_cdtemplate_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.contract_templates ALTER COLUMN cdtemplate ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.contract_templates_cdtemplate_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: contracts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contracts (
    cdcontract integer NOT NULL,
    dtcreation date,
    dstitle character varying(100),
    cdtemplate integer,
    cdproperty integer,
    cdindex integer,
    dtlimit date,
    cdstatus integer DEFAULT 1,
    cdnotary integer
);


--
-- Name: contracts_cdcontract_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.contracts ALTER COLUMN cdcontract ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.contracts_cdcontract_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: countries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.countries (
    cdcountry integer NOT NULL,
    nmcountry character varying(60),
    sgcountry character(3)
);


--
-- Name: countries_cdcountry_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.countries ALTER COLUMN cdcountry ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.countries_cdcountry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: districts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.districts (
    cddistrict integer NOT NULL,
    nmdistrict character varying(60),
    cdcity integer
);


--
-- Name: districts_cddistrict_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.districts ALTER COLUMN cddistrict ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.districts_cddistrict_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: index_rates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.index_rates (
    cdrate integer NOT NULL,
    refmonth integer,
    refyear integer,
    vlrate numeric(18,4),
    cdindex integer
);


--
-- Name: index_rates_cdrate_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.index_rates ALTER COLUMN cdrate ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.index_rates_cdrate_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: indexes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.indexes (
    cdindex integer NOT NULL,
    nmindex character varying(50)
);


--
-- Name: indexes_cdindex_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.indexes ALTER COLUMN cdindex ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.indexes_cdindex_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: installments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.installments (
    cdinstallment integer NOT NULL,
    dtdue date,
    vlbase numeric(15,2),
    vladjusted numeric(15,2),
    cdstatus integer,
    dtpayment date,
    nrinstallment integer,
    cdcontract integer,
    vlpenalty numeric(12,2),
    vlinterest numeric(12,2)
);


--
-- Name: installments_cdinstallment_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.installments ALTER COLUMN cdinstallment ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.installments_cdinstallment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notaries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notaries (
    cdnotary integer NOT NULL,
    cdcity integer,
    book character varying(50),
    leaf character varying(50),
    dt date,
    nrnotary integer
);


--
-- Name: notaries_cdnotary_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notaries ALTER COLUMN cdnotary ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notaries_cdnotary_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notification_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification_templates (
    cdnotificationtemplate integer NOT NULL,
    tpcode integer NOT NULL,
    nmtemplate character varying(100) NOT NULL,
    dsdescription text
);


--
-- Name: notification_templates_cdnotificationtemplate_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notification_templates ALTER COLUMN cdnotificationtemplate ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notification_templates_cdnotificationtemplate_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    cdnotification integer NOT NULL,
    dsmessage text,
    dtsend date,
    cdcontract integer,
    cduser integer,
    cdnotificationtemplate integer,
    fgchannel integer DEFAULT 1 NOT NULL
);


--
-- Name: notifications_cdnotification_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notifications ALTER COLUMN cdnotification ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notifications_cdnotification_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: occupations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.occupations (
    cdoccupation integer NOT NULL,
    nmoccupation character varying(100)
);


--
-- Name: occupations_cdoccupation_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.occupations ALTER COLUMN cdoccupation ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.occupations_cdoccupation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: properties; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.properties (
    cdproperty integer NOT NULL,
    nrregistration character varying(30),
    dsdescription text,
    vltotalarea numeric(10,2),
    cdaddress integer,
    cdtype integer,
    cdpurpose integer,
    cdstatus integer
);


--
-- Name: properties_cdproperty_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.properties ALTER COLUMN cdproperty ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.properties_cdproperty_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: properties_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.properties_users (
    cdproperty integer NOT NULL,
    cduser integer NOT NULL
);


--
-- Name: property_purposes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.property_purposes (
    cdpurpose integer NOT NULL,
    nmpurpose character varying(20)
);


--
-- Name: property_purposes_cdpurpose_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.property_purposes ALTER COLUMN cdpurpose ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.property_purposes_cdpurpose_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: property_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.property_status (
    cdstatus integer NOT NULL,
    nmstatus character varying(30)
);


--
-- Name: property_status_cdstatus_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.property_status ALTER COLUMN cdstatus ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.property_status_cdstatus_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: property_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.property_types (
    cdtype integer NOT NULL,
    nmtype character varying(30)
);


--
-- Name: property_types_cdtype_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.property_types ALTER COLUMN cdtype ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.property_types_cdtype_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: readjustment_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.readjustment_logs (
    cdlog integer NOT NULL,
    cdcontract integer,
    cdinstallment integer,
    cdindex integer,
    vlold numeric(15,2),
    vlnew numeric(15,2),
    dtreadjustment date
);


--
-- Name: readjustment_logs_cdlog_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.readjustment_logs ALTER COLUMN cdlog ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.readjustment_logs_cdlog_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    cdrole integer NOT NULL,
    nmrole character varying(50)
);


--
-- Name: roles_cdrole_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.roles ALTER COLUMN cdrole ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.roles_cdrole_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: states; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.states (
    cdstate integer NOT NULL,
    nmstate character varying(50),
    sgstate character(2),
    cdcountry integer
);


--
-- Name: states_cdstate_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.states ALTER COLUMN cdstate ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.states_cdstate_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: template_topics; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.template_topics (
    cdtopic integer NOT NULL,
    cdtemplate integer NOT NULL
);


--
-- Name: topics; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.topics (
    cdtopic integer NOT NULL,
    nmtopic character varying(100),
    nrorder integer
);


--
-- Name: topics_cdtopic_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.topics ALTER COLUMN cdtopic ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.topics_cdtopic_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_contract; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_contract (
    cdcontract integer NOT NULL,
    cduser integer NOT NULL,
    cdrole integer NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    cduser integer NOT NULL,
    dtbirth date,
    fgdocument boolean,
    document character varying(20),
    nmuser character varying(100),
    nrcellphone character varying(15),
    cdaddress integer,
    cdoccupation integer,
    dsissuingbody character varying(50)
);


--
-- Name: users_cduser_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.users ALTER COLUMN cduser ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.users_cduser_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (cdaddress);


--
-- Name: bank_accounts bank_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT bank_accounts_pkey PRIMARY KEY (cdbankaccount);


--
-- Name: cities cities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT cities_pkey PRIMARY KEY (cdcity);


--
-- Name: clauses clauses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clauses
    ADD CONSTRAINT clauses_pkey PRIMARY KEY (cdclause);


--
-- Name: contract_templates contract_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contract_templates
    ADD CONSTRAINT contract_templates_pkey PRIMARY KEY (cdtemplate);


--
-- Name: contracts contracts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT contracts_pkey PRIMARY KEY (cdcontract);


--
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (cdcountry);


--
-- Name: districts districts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.districts
    ADD CONSTRAINT districts_pkey PRIMARY KEY (cddistrict);


--
-- Name: index_rates index_rates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.index_rates
    ADD CONSTRAINT index_rates_pkey PRIMARY KEY (cdrate);


--
-- Name: indexes indexes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indexes
    ADD CONSTRAINT indexes_pkey PRIMARY KEY (cdindex);


--
-- Name: installments installments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.installments
    ADD CONSTRAINT installments_pkey PRIMARY KEY (cdinstallment);


--
-- Name: notaries notaries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notaries
    ADD CONSTRAINT notaries_pkey PRIMARY KEY (cdnotary);


--
-- Name: notification_templates notification_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_templates
    ADD CONSTRAINT notification_templates_pkey PRIMARY KEY (cdnotificationtemplate);


--
-- Name: notification_templates notification_templates_tpcode_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_templates
    ADD CONSTRAINT notification_templates_tpcode_key UNIQUE (tpcode);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (cdnotification);


--
-- Name: occupations occupations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.occupations
    ADD CONSTRAINT occupations_pkey PRIMARY KEY (cdoccupation);


--
-- Name: properties properties_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (cdproperty);


--
-- Name: properties_users properties_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties_users
    ADD CONSTRAINT properties_users_pkey PRIMARY KEY (cdproperty, cduser);


--
-- Name: property_purposes property_purposes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.property_purposes
    ADD CONSTRAINT property_purposes_pkey PRIMARY KEY (cdpurpose);


--
-- Name: property_status property_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.property_status
    ADD CONSTRAINT property_status_pkey PRIMARY KEY (cdstatus);


--
-- Name: property_types property_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.property_types
    ADD CONSTRAINT property_types_pkey PRIMARY KEY (cdtype);


--
-- Name: readjustment_logs readjustment_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.readjustment_logs
    ADD CONSTRAINT readjustment_logs_pkey PRIMARY KEY (cdlog);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (cdrole);


--
-- Name: states states_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.states
    ADD CONSTRAINT states_pkey PRIMARY KEY (cdstate);


--
-- Name: template_topics template_topics_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.template_topics
    ADD CONSTRAINT template_topics_pkey PRIMARY KEY (cdtopic, cdtemplate);


--
-- Name: topics topics_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (cdtopic);


--
-- Name: users uk_users_document; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_users_document UNIQUE (document);


--
-- Name: user_contract user_contract_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contract
    ADD CONSTRAINT user_contract_pkey PRIMARY KEY (cdcontract, cduser, cdrole);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (cduser);


--
-- Name: contracts contracts_cdnotary_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT contracts_cdnotary_fkey FOREIGN KEY (cdnotary) REFERENCES public.notaries(cdnotary);


--
-- Name: addresses fk_addresses_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT fk_addresses_2 FOREIGN KEY (cddistrict) REFERENCES public.districts(cddistrict) ON DELETE RESTRICT;


--
-- Name: bank_accounts fk_bank_accounts_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT fk_bank_accounts_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser) ON DELETE RESTRICT;


--
-- Name: cities fk_cities_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT fk_cities_2 FOREIGN KEY (cdstate) REFERENCES public.states(cdstate) ON DELETE RESTRICT;


--
-- Name: clauses fk_clauses_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clauses
    ADD CONSTRAINT fk_clauses_2 FOREIGN KEY (cdtopic) REFERENCES public.topics(cdtopic) ON DELETE RESTRICT;


--
-- Name: contracts fk_contracts_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_2 FOREIGN KEY (cdtemplate) REFERENCES public.contract_templates(cdtemplate) ON DELETE CASCADE;


--
-- Name: contracts fk_contracts_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_3 FOREIGN KEY (cdproperty) REFERENCES public.properties(cdproperty) ON DELETE SET NULL;


--
-- Name: contracts fk_contracts_4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_4 FOREIGN KEY (cdindex) REFERENCES public.indexes(cdindex) ON DELETE SET NULL;


--
-- Name: districts fk_districts_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.districts
    ADD CONSTRAINT fk_districts_2 FOREIGN KEY (cdcity) REFERENCES public.cities(cdcity) ON DELETE RESTRICT;


--
-- Name: index_rates fk_index_rates_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.index_rates
    ADD CONSTRAINT fk_index_rates_2 FOREIGN KEY (cdindex) REFERENCES public.indexes(cdindex) ON DELETE RESTRICT;


--
-- Name: installments fk_installments_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.installments
    ADD CONSTRAINT fk_installments_2 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- Name: notaries fk_notaries_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notaries
    ADD CONSTRAINT fk_notaries_1 FOREIGN KEY (cdcity) REFERENCES public.cities(cdcity) ON DELETE RESTRICT;


--
-- Name: notifications fk_notifications_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk_notifications_2 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- Name: notifications fk_notifications_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk_notifications_3 FOREIGN KEY (cduser) REFERENCES public.users(cduser) ON DELETE RESTRICT;


--
-- Name: notifications fk_notifications_template; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk_notifications_template FOREIGN KEY (cdnotificationtemplate) REFERENCES public.notification_templates(cdnotificationtemplate);


--
-- Name: properties fk_properties_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_2 FOREIGN KEY (cdaddress) REFERENCES public.addresses(cdaddress) ON DELETE CASCADE;


--
-- Name: properties fk_properties_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_3 FOREIGN KEY (cdtype) REFERENCES public.property_types(cdtype) ON DELETE CASCADE;


--
-- Name: properties fk_properties_4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_4 FOREIGN KEY (cdpurpose) REFERENCES public.property_purposes(cdpurpose) ON DELETE CASCADE;


--
-- Name: properties fk_properties_5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_5 FOREIGN KEY (cdstatus) REFERENCES public.property_status(cdstatus) ON DELETE CASCADE;


--
-- Name: properties_users fk_properties_users_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties_users
    ADD CONSTRAINT fk_properties_users_1 FOREIGN KEY (cdproperty) REFERENCES public.properties(cdproperty);


--
-- Name: properties_users fk_properties_users_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.properties_users
    ADD CONSTRAINT fk_properties_users_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser);


--
-- Name: states fk_states_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.states
    ADD CONSTRAINT fk_states_2 FOREIGN KEY (cdcountry) REFERENCES public.countries(cdcountry) ON DELETE RESTRICT;


--
-- Name: template_topics fk_template_topics_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.template_topics
    ADD CONSTRAINT fk_template_topics_1 FOREIGN KEY (cdtopic) REFERENCES public.topics(cdtopic);


--
-- Name: template_topics fk_template_topics_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.template_topics
    ADD CONSTRAINT fk_template_topics_2 FOREIGN KEY (cdtemplate) REFERENCES public.contract_templates(cdtemplate);


--
-- Name: user_contract fk_user_contract_1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contract
    ADD CONSTRAINT fk_user_contract_1 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract);


--
-- Name: user_contract fk_user_contract_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contract
    ADD CONSTRAINT fk_user_contract_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser);


--
-- Name: user_contract fk_user_contract_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contract
    ADD CONSTRAINT fk_user_contract_3 FOREIGN KEY (cdrole) REFERENCES public.roles(cdrole);


--
-- Name: users fk_users_2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_2 FOREIGN KEY (cdaddress) REFERENCES public.addresses(cdaddress) ON DELETE RESTRICT;


--
-- Name: users fk_users_3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_3 FOREIGN KEY (cdoccupation) REFERENCES public.occupations(cdoccupation) ON DELETE CASCADE;

--
-- Name: readjustment_logs readjustment_logs_cdcontract_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.readjustment_logs
    ADD CONSTRAINT readjustment_logs_cdcontract_fkey FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract);


--
-- Name: readjustment_logs readjustment_logs_cdindex_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.readjustment_logs
    ADD CONSTRAINT readjustment_logs_cdindex_fkey FOREIGN KEY (cdindex) REFERENCES public.indexes(cdindex);


--
-- Name: readjustment_logs readjustment_logs_cdinstallment_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.readjustment_logs
    ADD CONSTRAINT readjustment_logs_cdinstallment_fkey FOREIGN KEY (cdinstallment) REFERENCES public.installments(cdinstallment);


--
-- PostgreSQL database dump complete
--

\unrestrict L9ftLX4gdSxUYlk501tN5uXUgaI2y76wHxCF2C63tzE7Mag4n66Hecp2WtkyRUB

