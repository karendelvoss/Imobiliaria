--
-- PostgreSQL database dump
--

\restrict lmTdKCoIh5szaJGiUUetwbseDnazE0UqOHblgdWuYrMp3HW09eT1lrcI1npTNku

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-04-22 01:56:53

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 17793)
-- Name: addresses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.addresses (
    cdaddress integer NOT NULL,
    cdzipcode character(8),
    nmstreet character varying(100),
    nraddress character varying(10),
    dscomplement character varying(50),
    cddistrict integer
);


ALTER TABLE public.addresses OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 17864)
-- Name: bank_accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bank_accounts (
    cdbankaccount integer NOT NULL,
    nragency character varying(30),
    nraccount character varying(30),
    nrpixkey character varying(100),
    cduser integer
);


ALTER TABLE public.bank_accounts OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 17858)
-- Name: broker_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.broker_data (
    nrcreci character varying(20),
    cduser integer NOT NULL
);


ALTER TABLE public.broker_data OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 17811)
-- Name: cities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cities (
    cdcity integer NOT NULL,
    nmcity character varying(60),
    cdstate integer
);


ALTER TABLE public.cities OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 17844)
-- Name: clauses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clauses (
    cdclause integer NOT NULL,
    dstext text,
    cdtopic integer
);


ALTER TABLE public.clauses OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 17878)
-- Name: commissions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commissions (
    cdcommission integer NOT NULL,
    vlcommission numeric(12,2),
    dtpayment date,
    cdcontract integer,
    cduser integer
);


ALTER TABLE public.commissions OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 17908)
-- Name: contract_templates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contract_templates (
    cdtemplate integer NOT NULL,
    nmtemplate character varying(100),
    dsversion character varying(10),
    fgactive character(1)
);


ALTER TABLE public.contract_templates OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 17832)
-- Name: contracts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contracts (
    cdcontract integer NOT NULL,
    dtcreation date,
    dstitle character varying(100),
    cdtemplate integer,
    cdproperty integer,
    cdindex integer
);


ALTER TABLE public.contracts OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 17799)
-- Name: countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries (
    cdcountry integer NOT NULL,
    nmcountry character varying(60),
    sgcountry character(3)
);


ALTER TABLE public.countries OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 17817)
-- Name: districts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.districts (
    cddistrict integer NOT NULL,
    nmdistrict character varying(60),
    cdcity integer
);


ALTER TABLE public.districts OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 17923)
-- Name: index_rates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.index_rates (
    cdrate integer NOT NULL,
    refmonth integer,
    refyear integer,
    vlrate numeric(10,4),
    fk_indexes_cdindex integer
);


ALTER TABLE public.index_rates OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 17917)
-- Name: indexes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.indexes (
    cdindex integer NOT NULL,
    nmindex character varying(50)
);


ALTER TABLE public.indexes OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 17929)
-- Name: installments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.installments (
    cdinstallment integer NOT NULL,
    dtdue date,
    vlbase numeric(12,2),
    vladjusted numeric(12,2),
    cdstatus integer,
    dtpayment date,
    nrinstallment integer,
    fk_contracts_cdcontract integer
);


ALTER TABLE public.installments OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 17935)
-- Name: notifications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notifications (
    cdnotification integer NOT NULL,
    dsmessage text,
    dtsend date,
    fgread boolean,
    cdcontract integer,
    cduser integer
);


ALTER TABLE public.notifications OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 17902)
-- Name: occupations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.occupations (
    cdoccupation integer NOT NULL,
    nmoccupation character varying(100)
);


ALTER TABLE public.occupations OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 17870)
-- Name: properties; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.properties (
    cdproperty integer NOT NULL,
    nrregistration character varying(30),
    dsdescription text,
    vltotalarea_ numeric(10,2),
    cdaddress integer,
    cdtype integer,
    cdpurpose integer,
    cdstatus integer
);


ALTER TABLE public.properties OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 17943)
-- Name: properties_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.properties_users (
    cdproperty integer,
    cduser integer
);


ALTER TABLE public.properties_users OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 17890)
-- Name: property_purposes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.property_purposes (
    cdpurpose integer NOT NULL,
    nmpurpose character varying(20)
);


ALTER TABLE public.property_purposes OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 17896)
-- Name: property_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.property_status (
    cdstatus integer NOT NULL,
    nmstatus character varying(30)
);


ALTER TABLE public.property_status OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 17884)
-- Name: property_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.property_types (
    cdtype integer NOT NULL,
    nmtype character varying(30)
);


ALTER TABLE public.property_types OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 17823)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    cdrole integer NOT NULL,
    nmrole character varying(50)
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 17805)
-- Name: states; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.states (
    cdstate integer NOT NULL,
    nmstate character varying(50),
    sgstate character(2),
    cdcountry integer
);


ALTER TABLE public.states OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 17914)
-- Name: template_topics_topics_contract_templates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.template_topics_topics_contract_templates (
    cdtopic integer,
    cdtemplate integer
);


ALTER TABLE public.template_topics_topics_contract_templates OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 17838)
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.topics (
    cdtopic integer NOT NULL,
    nmtopic character varying(100)
);


ALTER TABLE public.topics OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 17829)
-- Name: user_contract_contracts_users_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_contract_contracts_users_roles (
    cdcontract integer,
    cduser integer,
    cdrole integer
);


ALTER TABLE public.user_contract_contracts_users_roles OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 17787)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    cduser integer NOT NULL,
    dtbirth date,
    fgdocument integer,
    document character varying(20),
    nmuser character varying(100),
    nrcellphone character varying(15),
    cdaddress integer,
    cdoccupation integer
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 17852)
-- Name: variables; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.variables (
    cdvariable integer NOT NULL,
    nmvariable character varying(50),
    vlvariable character varying(255),
    tpvariable character varying(50),
    fgtriggeralert boolean,
    cdcontract integer
);


ALTER TABLE public.variables OWNER TO postgres;

--
-- TOC entry 5185 (class 0 OID 17793)
-- Dependencies: 220
-- Data for Name: addresses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.addresses (cdaddress, cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) FROM stdin;
1	89201000	Rua das Palmeiras	100	\N	2
2	89216200	Rua XV de Novembro	2500	Bloco B	1
3	89203000	Rua Otto Boehm	450	Sala 101	2
\.


--
-- TOC entry 5197 (class 0 OID 17864)
-- Dependencies: 232
-- Data for Name: bank_accounts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bank_accounts (cdbankaccount, nragency, nraccount, nrpixkey, cduser) FROM stdin;
1	001	12345-6	000.111.222-33	1
2	033	98765-4	marta.silva@email.com	2
\.


--
-- TOC entry 5196 (class 0 OID 17858)
-- Dependencies: 231
-- Data for Name: broker_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.broker_data (nrcreci, cduser) FROM stdin;
CRECI-SC-12345	3
\.


--
-- TOC entry 5188 (class 0 OID 17811)
-- Dependencies: 223
-- Data for Name: cities; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cities (cdcity, nmcity, cdstate) FROM stdin;
1	Joinville	1
\.


--
-- TOC entry 5194 (class 0 OID 17844)
-- Dependencies: 229
-- Data for Name: clauses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.clauses (cdclause, dstext, cdtopic) FROM stdin;
1	O presente contrato tem por objeto a locação do imóvel matriculado sob nº MAT-1001.	1
\.


--
-- TOC entry 5199 (class 0 OID 17878)
-- Dependencies: 234
-- Data for Name: commissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commissions (cdcommission, vlcommission, dtpayment, cdcontract, cduser) FROM stdin;
1	1500.00	2026-04-21	1	3
\.


--
-- TOC entry 5204 (class 0 OID 17908)
-- Dependencies: 239
-- Data for Name: contract_templates; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contract_templates (cdtemplate, nmtemplate, dsversion, fgactive) FROM stdin;
1	Contrato de Locação Residencial	V1.0	S
2	Contrato de venda	1	S
\.


--
-- TOC entry 5192 (class 0 OID 17832)
-- Dependencies: 227
-- Data for Name: contracts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contracts (cdcontract, dtcreation, dstitle, cdtemplate, cdproperty, cdindex) FROM stdin;
1	2026-04-20	Locacao Ap Central - MAT-1001	1	1	1
2	2026-04-22	Venda casinha legal	2	4	1
3	2026-04-22	Locacao bem complicada	1	3	3
\.


--
-- TOC entry 5186 (class 0 OID 17799)
-- Dependencies: 221
-- Data for Name: countries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.countries (cdcountry, nmcountry, sgcountry) FROM stdin;
1	Brasil	BRA
\.


--
-- TOC entry 5189 (class 0 OID 17817)
-- Dependencies: 224
-- Data for Name: districts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.districts (cddistrict, nmdistrict, cdcity) FROM stdin;
1	Atiradores	1
2	Centro	1
\.


--
-- TOC entry 5207 (class 0 OID 17923)
-- Dependencies: 242
-- Data for Name: index_rates; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.index_rates (cdrate, refmonth, refyear, vlrate, fk_indexes_cdindex) FROM stdin;
1	4	2026	0.0045	1
\.


--
-- TOC entry 5206 (class 0 OID 17917)
-- Dependencies: 241
-- Data for Name: indexes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.indexes (cdindex, nmindex) FROM stdin;
1	IPCA
2	IGP-M
3	CUB
\.


--
-- TOC entry 5208 (class 0 OID 17929)
-- Dependencies: 243
-- Data for Name: installments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.installments (cdinstallment, dtdue, vlbase, vladjusted, cdstatus, dtpayment, nrinstallment, fk_contracts_cdcontract) FROM stdin;
1	2026-05-10	2500.00	2500.00	1	\N	1	1
2	2026-06-10	2500.00	2500.00	1	\N	2	1
3	2026-05-22	100000.00	\N	1	\N	1	2
4	2026-06-22	200000.00	\N	1	\N	2	2
5	2026-07-22	300000.00	\N	1	\N	3	2
6	2026-08-22	10000.00	\N	1	\N	4	2
7	2026-09-22	50000.00	\N	1	\N	5	2
8	2026-05-22	2500.00	\N	1	\N	1	3
9	2026-06-22	2500.00	\N	1	\N	2	3
10	2026-07-22	2500.00	\N	1	\N	3	3
11	2026-08-22	2500.00	\N	1	\N	4	3
12	2026-09-22	2500.00	\N	1	\N	5	3
13	2026-10-22	2500.00	\N	1	\N	6	3
14	2026-11-22	2500.00	\N	1	\N	7	3
15	2026-12-22	2500.00	\N	1	\N	8	3
16	2027-01-22	2500.00	\N	1	\N	9	3
17	2027-02-22	2500.00	\N	1	\N	10	3
18	2027-03-22	2500.00	\N	1	\N	11	3
19	2027-04-22	2500.00	\N	1	\N	12	3
\.


--
-- TOC entry 5209 (class 0 OID 17935)
-- Dependencies: 244
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.notifications (cdnotification, dsmessage, dtsend, fgread, cdcontract, cduser) FROM stdin;
1	Seu contrato de locação foi gerado com sucesso!	2026-04-20	f	1	1
2	A parcela esta proxima do vencimento!	2026-10-05	f	1	4
\.


--
-- TOC entry 5203 (class 0 OID 17902)
-- Dependencies: 238
-- Data for Name: occupations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.occupations (cdoccupation, nmoccupation) FROM stdin;
1	Engenheiro de Software
2	Médico
3	Corretor de Imóveis
\.


--
-- TOC entry 5198 (class 0 OID 17870)
-- Dependencies: 233
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.properties (cdproperty, nrregistration, dsdescription, vltotalarea_, cdaddress, cdtype, cdpurpose, cdstatus) FROM stdin;
1	MAT-1001	Apartamento mobiliado no Centro	65.50	1	2	1	2
2	MAT-1002	Casa com piscina Atiradores	250.00	2	1	1	1
4	MAT-1011	Casinha rosa no fim da rua	70.00	3	1	1	3
3	MAT-1003	Sala comercial reformada	45.00	3	3	2	2
\.


--
-- TOC entry 5210 (class 0 OID 17943)
-- Dependencies: 245
-- Data for Name: properties_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.properties_users (cdproperty, cduser) FROM stdin;
1	2
2	2
1	1
1	3
4	5
\.


--
-- TOC entry 5201 (class 0 OID 17890)
-- Dependencies: 236
-- Data for Name: property_purposes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.property_purposes (cdpurpose, nmpurpose) FROM stdin;
1	Residencial
2	Comercial
3	Industrial
\.


--
-- TOC entry 5202 (class 0 OID 17896)
-- Dependencies: 237
-- Data for Name: property_status; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.property_status (cdstatus, nmstatus) FROM stdin;
1	Disponível
2	Alugado
3	Vendido
4	Reservado
\.


--
-- TOC entry 5200 (class 0 OID 17884)
-- Dependencies: 235
-- Data for Name: property_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.property_types (cdtype, nmtype) FROM stdin;
1	Casa
2	Apartamento
3	Sala Comercial
4	Terreno
\.


--
-- TOC entry 5190 (class 0 OID 17823)
-- Dependencies: 225
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (cdrole, nmrole) FROM stdin;
1	Proprietário
2	Locatário
3	Fiador
4	Comprador
5	Comissionado
\.


--
-- TOC entry 5187 (class 0 OID 17805)
-- Dependencies: 222
-- Data for Name: states; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.states (cdstate, nmstate, sgstate, cdcountry) FROM stdin;
1	Santa Catarina	SC	1
\.


--
-- TOC entry 5205 (class 0 OID 17914)
-- Dependencies: 240
-- Data for Name: template_topics_topics_contract_templates; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.template_topics_topics_contract_templates (cdtopic, cdtemplate) FROM stdin;
1	1
2	1
3	1
\.


--
-- TOC entry 5193 (class 0 OID 17838)
-- Dependencies: 228
-- Data for Name: topics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.topics (cdtopic, nmtopic) FROM stdin;
1	Objeto do Contrato
2	Valores e Pagamentos
3	Rescisão e Multas
\.


--
-- TOC entry 5191 (class 0 OID 17829)
-- Dependencies: 226
-- Data for Name: user_contract_contracts_users_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_contract_contracts_users_roles (cdcontract, cduser, cdrole) FROM stdin;
1	2	1
1	1	2
2	5	1
2	1	4
3	4	2
\.


--
-- TOC entry 5184 (class 0 OID 17787)
-- Dependencies: 219
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (cduser, dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation) FROM stdin;
2	1985-10-20	1	444.555.666-77	Marta Silva	(47) 98888-2222	2	2
3	1992-03-12	1	888.999.000-11	Ricardo Santos	(47) 97777-3333	3	3
1	1990-05-15	0	000.111.222-33	Karen Maria	(47) 99999-1111	1	1
4	2000-10-05	0	111.111.111-11	Suzano Pereira	(47) 99999-9999	2	1
5	2004-10-10	0	111.111.111-11	Andre Henrique	(47) 99999-9999	3	3
\.


--
-- TOC entry 5195 (class 0 OID 17852)
-- Dependencies: 230
-- Data for Name: variables; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.variables (cdvariable, nmvariable, vlvariable, tpvariable, fgtriggeralert, cdcontract) FROM stdin;
1	Valor_Multa_Rescisao	5000.00	Decimal	t	1
\.


--
-- TOC entry 4962 (class 2606 OID 17798)
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (cdaddress);


--
-- TOC entry 4984 (class 2606 OID 17869)
-- Name: bank_accounts bank_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT bank_accounts_pkey PRIMARY KEY (cdbankaccount);


--
-- TOC entry 4982 (class 2606 OID 17863)
-- Name: broker_data broker_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.broker_data
    ADD CONSTRAINT broker_data_pkey PRIMARY KEY (cduser);


--
-- TOC entry 4968 (class 2606 OID 17816)
-- Name: cities cities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT cities_pkey PRIMARY KEY (cdcity);


--
-- TOC entry 4978 (class 2606 OID 17851)
-- Name: clauses clauses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clauses
    ADD CONSTRAINT clauses_pkey PRIMARY KEY (cdclause);


--
-- TOC entry 4988 (class 2606 OID 17883)
-- Name: commissions commissions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commissions
    ADD CONSTRAINT commissions_pkey PRIMARY KEY (cdcommission);


--
-- TOC entry 4998 (class 2606 OID 17913)
-- Name: contract_templates contract_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract_templates
    ADD CONSTRAINT contract_templates_pkey PRIMARY KEY (cdtemplate);


--
-- TOC entry 4974 (class 2606 OID 17837)
-- Name: contracts contracts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT contracts_pkey PRIMARY KEY (cdcontract);


--
-- TOC entry 4964 (class 2606 OID 17804)
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (cdcountry);


--
-- TOC entry 4970 (class 2606 OID 17822)
-- Name: districts districts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.districts
    ADD CONSTRAINT districts_pkey PRIMARY KEY (cddistrict);


--
-- TOC entry 5002 (class 2606 OID 17928)
-- Name: index_rates index_rates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.index_rates
    ADD CONSTRAINT index_rates_pkey PRIMARY KEY (cdrate);


--
-- TOC entry 5000 (class 2606 OID 17922)
-- Name: indexes indexes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.indexes
    ADD CONSTRAINT indexes_pkey PRIMARY KEY (cdindex);


--
-- TOC entry 5004 (class 2606 OID 17934)
-- Name: installments installments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.installments
    ADD CONSTRAINT installments_pkey PRIMARY KEY (cdinstallment);


--
-- TOC entry 5006 (class 2606 OID 17942)
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (cdnotification);


--
-- TOC entry 4996 (class 2606 OID 17907)
-- Name: occupations occupations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.occupations
    ADD CONSTRAINT occupations_pkey PRIMARY KEY (cdoccupation);


--
-- TOC entry 4986 (class 2606 OID 17877)
-- Name: properties properties_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT properties_pkey PRIMARY KEY (cdproperty);


--
-- TOC entry 4992 (class 2606 OID 17895)
-- Name: property_purposes property_purposes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_purposes
    ADD CONSTRAINT property_purposes_pkey PRIMARY KEY (cdpurpose);


--
-- TOC entry 4994 (class 2606 OID 17901)
-- Name: property_status property_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_status
    ADD CONSTRAINT property_status_pkey PRIMARY KEY (cdstatus);


--
-- TOC entry 4990 (class 2606 OID 17889)
-- Name: property_types property_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.property_types
    ADD CONSTRAINT property_types_pkey PRIMARY KEY (cdtype);


--
-- TOC entry 4972 (class 2606 OID 17828)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (cdrole);


--
-- TOC entry 4966 (class 2606 OID 17810)
-- Name: states states_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.states
    ADD CONSTRAINT states_pkey PRIMARY KEY (cdstate);


--
-- TOC entry 4976 (class 2606 OID 17843)
-- Name: topics topics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (cdtopic);


--
-- TOC entry 4960 (class 2606 OID 17792)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (cduser);


--
-- TOC entry 4980 (class 2606 OID 17857)
-- Name: variables variables_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.variables
    ADD CONSTRAINT variables_pkey PRIMARY KEY (cdvariable);


--
-- TOC entry 5009 (class 2606 OID 17956)
-- Name: addresses fk_addresses_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT fk_addresses_2 FOREIGN KEY (cddistrict) REFERENCES public.districts(cddistrict) ON DELETE RESTRICT;


--
-- TOC entry 5022 (class 2606 OID 18021)
-- Name: bank_accounts fk_bank_accounts_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT fk_bank_accounts_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser) ON DELETE RESTRICT;


--
-- TOC entry 5021 (class 2606 OID 18016)
-- Name: broker_data fk_broker_data_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.broker_data
    ADD CONSTRAINT fk_broker_data_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser);


--
-- TOC entry 5011 (class 2606 OID 17966)
-- Name: cities fk_cities_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT fk_cities_2 FOREIGN KEY (cdstate) REFERENCES public.states(cdstate) ON DELETE RESTRICT;


--
-- TOC entry 5019 (class 2606 OID 18006)
-- Name: clauses fk_clauses_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clauses
    ADD CONSTRAINT fk_clauses_2 FOREIGN KEY (cdtopic) REFERENCES public.topics(cdtopic) ON DELETE RESTRICT;


--
-- TOC entry 5027 (class 2606 OID 18046)
-- Name: commissions fk_commissions_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commissions
    ADD CONSTRAINT fk_commissions_2 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- TOC entry 5028 (class 2606 OID 18051)
-- Name: commissions fk_commissions_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commissions
    ADD CONSTRAINT fk_commissions_3 FOREIGN KEY (cduser) REFERENCES public.broker_data(cduser);


--
-- TOC entry 5016 (class 2606 OID 17991)
-- Name: contracts fk_contracts_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_2 FOREIGN KEY (cdtemplate) REFERENCES public.contract_templates(cdtemplate) ON DELETE CASCADE;


--
-- TOC entry 5017 (class 2606 OID 17996)
-- Name: contracts fk_contracts_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_3 FOREIGN KEY (cdproperty) REFERENCES public.properties(cdproperty) ON DELETE SET NULL;


--
-- TOC entry 5018 (class 2606 OID 18001)
-- Name: contracts fk_contracts_4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contracts
    ADD CONSTRAINT fk_contracts_4 FOREIGN KEY (cdindex) REFERENCES public.indexes(cdindex) ON DELETE SET NULL;


--
-- TOC entry 5012 (class 2606 OID 17971)
-- Name: districts fk_districts_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.districts
    ADD CONSTRAINT fk_districts_2 FOREIGN KEY (cdcity) REFERENCES public.cities(cdcity) ON DELETE RESTRICT;


--
-- TOC entry 5031 (class 2606 OID 18066)
-- Name: index_rates fk_index_rates_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.index_rates
    ADD CONSTRAINT fk_index_rates_2 FOREIGN KEY (fk_indexes_cdindex) REFERENCES public.indexes(cdindex) ON DELETE RESTRICT;


--
-- TOC entry 5032 (class 2606 OID 18071)
-- Name: installments fk_installments_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.installments
    ADD CONSTRAINT fk_installments_2 FOREIGN KEY (fk_contracts_cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- TOC entry 5033 (class 2606 OID 18076)
-- Name: notifications fk_notifications_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk_notifications_2 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- TOC entry 5034 (class 2606 OID 18081)
-- Name: notifications fk_notifications_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk_notifications_3 FOREIGN KEY (cduser) REFERENCES public.users(cduser) ON DELETE RESTRICT;


--
-- TOC entry 5023 (class 2606 OID 18026)
-- Name: properties fk_properties_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_2 FOREIGN KEY (cdaddress) REFERENCES public.addresses(cdaddress) ON DELETE CASCADE;


--
-- TOC entry 5024 (class 2606 OID 18031)
-- Name: properties fk_properties_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_3 FOREIGN KEY (cdtype) REFERENCES public.property_types(cdtype) ON DELETE CASCADE;


--
-- TOC entry 5025 (class 2606 OID 18036)
-- Name: properties fk_properties_4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_4 FOREIGN KEY (cdpurpose) REFERENCES public.property_purposes(cdpurpose) ON DELETE CASCADE;


--
-- TOC entry 5026 (class 2606 OID 18041)
-- Name: properties fk_properties_5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties
    ADD CONSTRAINT fk_properties_5 FOREIGN KEY (cdstatus) REFERENCES public.property_status(cdstatus) ON DELETE CASCADE;


--
-- TOC entry 5010 (class 2606 OID 17961)
-- Name: states fk_states_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.states
    ADD CONSTRAINT fk_states_2 FOREIGN KEY (cdcountry) REFERENCES public.countries(cdcountry) ON DELETE RESTRICT;


--
-- TOC entry 5029 (class 2606 OID 18056)
-- Name: template_topics_topics_contract_templates fk_template_topics_topics_contract_templates_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.template_topics_topics_contract_templates
    ADD CONSTRAINT fk_template_topics_topics_contract_templates_1 FOREIGN KEY (cdtopic) REFERENCES public.topics(cdtopic);


--
-- TOC entry 5030 (class 2606 OID 18061)
-- Name: template_topics_topics_contract_templates fk_template_topics_topics_contract_templates_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.template_topics_topics_contract_templates
    ADD CONSTRAINT fk_template_topics_topics_contract_templates_2 FOREIGN KEY (cdtemplate) REFERENCES public.contract_templates(cdtemplate);


--
-- TOC entry 5013 (class 2606 OID 17976)
-- Name: user_contract_contracts_users_roles fk_user_contract_contracts_users_roles_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_contract_contracts_users_roles
    ADD CONSTRAINT fk_user_contract_contracts_users_roles_1 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract);


--
-- TOC entry 5014 (class 2606 OID 17981)
-- Name: user_contract_contracts_users_roles fk_user_contract_contracts_users_roles_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_contract_contracts_users_roles
    ADD CONSTRAINT fk_user_contract_contracts_users_roles_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser);


--
-- TOC entry 5015 (class 2606 OID 17986)
-- Name: user_contract_contracts_users_roles fk_user_contract_contracts_users_roles_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_contract_contracts_users_roles
    ADD CONSTRAINT fk_user_contract_contracts_users_roles_3 FOREIGN KEY (cdrole) REFERENCES public.roles(cdrole);


--
-- TOC entry 5007 (class 2606 OID 17946)
-- Name: users fk_users_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_2 FOREIGN KEY (cdaddress) REFERENCES public.addresses(cdaddress) ON DELETE RESTRICT;


--
-- TOC entry 5008 (class 2606 OID 17951)
-- Name: users fk_users_3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_3 FOREIGN KEY (cdoccupation) REFERENCES public.occupations(cdoccupation) ON DELETE CASCADE;


--
-- TOC entry 5020 (class 2606 OID 18011)
-- Name: variables fk_variables_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.variables
    ADD CONSTRAINT fk_variables_2 FOREIGN KEY (cdcontract) REFERENCES public.contracts(cdcontract) ON DELETE RESTRICT;


--
-- TOC entry 5035 (class 2606 OID 18086)
-- Name: properties_users fkproperties_users_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties_users
    ADD CONSTRAINT fkproperties_users_1 FOREIGN KEY (cdproperty) REFERENCES public.properties(cdproperty);


--
-- TOC entry 5036 (class 2606 OID 18091)
-- Name: properties_users fkproperties_users_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.properties_users
    ADD CONSTRAINT fkproperties_users_2 FOREIGN KEY (cduser) REFERENCES public.users(cduser);


-- Completed on 2026-04-22 01:56:54

--
-- PostgreSQL database dump complete
--

\unrestrict lmTdKCoIh5szaJGiUUetwbseDnazE0UqOHblgdWuYrMp3HW09eT1lrcI1npTNku

