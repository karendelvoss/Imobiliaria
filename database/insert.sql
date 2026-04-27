--
-- PostgreSQL database dump
--

\restrict ZsrMWNrQYDQ0GdUUz51ZGnCYS3LQbw0ErzvHJoImGt2gajYffBtU3PXYyhEBCOK

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-04-25 22:24:20

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
-- TOC entry 5119 (class 0 OID 26043)
-- Dependencies: 224
-- Data for Name: countries; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.countries (cdcountry, nmcountry, sgcountry) FROM stdin;
1	Brasil	BRA
\.


--
-- TOC entry 5121 (class 0 OID 26050)
-- Dependencies: 226
-- Data for Name: states; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.states (cdstate, nmstate, sgstate, cdcountry) FROM stdin;
1	Santa Catarina	SC	1
\.


--
-- TOC entry 5123 (class 0 OID 26057)
-- Dependencies: 228
-- Data for Name: cities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cities (cdcity, nmcity, cdstate) FROM stdin;
1	Joinville	1
\.


--
-- TOC entry 5125 (class 0 OID 26064)
-- Dependencies: 230
-- Data for Name: districts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.districts (cddistrict, nmdistrict, cdcity) FROM stdin;
1	Centro	1
\.


--
-- TOC entry 5117 (class 0 OID 26036)
-- Dependencies: 222
-- Data for Name: addresses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.addresses (cdaddress, cdzipcode, nmstreet, nraddress, dscomplement, cddistrict) FROM stdin;
1	89200000	Rua XV de Novembro	1000	Sala 2	1
\.


--
-- TOC entry 5151 (class 0 OID 26166)
-- Dependencies: 256
-- Data for Name: occupations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.occupations (cdoccupation, nmoccupation) FROM stdin;
1	Analista de Sistemas
\.


--
-- TOC entry 5115 (class 0 OID 26029)
-- Dependencies: 220
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (cduser, dtbirth, fgdocument, document, nmuser, nrcellphone, cdaddress, cdoccupation, dsissuingbody) FROM stdin;
1	1990-05-20	t	12345678900	João Silva	47999999999	1	1	SSP
2	2004-08-30	t	12435611955	Kauane	47 9956-8956	1	1	SSP
3	1975-03-10	t	11111111101	Locador Job1 ContratoA	47991110001	1	1	SSP/SC
4	1990-07-20	t	11111111102	Locatario Job1 ContratoA VenceEm7Dias	47991110002	1	1	SSP/SC
5	1968-11-05	t	11111111103	Locador Job1 ContratoB	47991110003	1	1	SSP/SC
6	1995-01-15	t	11111111104	Locatario Job1 ContratoB VenceHoje	47991110004	1	1	SSP/SC
7	1970-06-22	t	22222222201	Locador Job2 ContratoA PagamentoRecebido	47992220001	1	1	SSP/SC
8	1988-09-30	t	22222222202	Locatario Job2 ContratoA PagouHoje	47992220002	1	1	SSP/SC
9	1965-04-18	t	22222222203	Locador Job2 ContratoB PagamentoRecebido	47992220003	1	1	SSP/SC
10	1993-12-01	t	22222222204	Locatario Job2 ContratoB PagouHoje	47992220004	1	1	SSP/SC
11	1960-08-14	t	33333333301	Locador Job3 ContratoA AniversarioAbril	47993330001	1	1	SSP/SC
12	1985-05-25	t	33333333302	Locatario Job3 ContratoA AniversarioAbril	47993330002	1	1	SSP/SC
13	1972-02-28	t	33333333303	Locador Job3 ContratoB AniversarioAbril	47993330003	1	1	SSP/SC
14	1998-10-10	t	33333333304	Locatario Job3 ContratoB AniversarioAbril	47993330004	1	1	SSP/SC
15	1955-07-07	t	44444444401	Locador Job4 ContratoA VenceEm3Dias	47994440001	1	1	SSP/SC
16	1992-03-14	t	44444444402	Locatario Job4 ContratoA VenceEm3Dias	47994440002	1	1	SSP/SC
17	1963-09-19	t	44444444403	Locador Job4 ContratoB VenceHoje	47994440003	1	1	SSP/SC
18	1991-06-03	t	44444444404	Locatario Job4 ContratoB VenceHoje	47994440004	1	1	SSP/SC
\.


--
-- TOC entry 5139 (class 0 OID 26122)
-- Dependencies: 244
-- Data for Name: bank_accounts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.bank_accounts (cdbankaccount, nragency, nraccount, nrpixkey, cduser) FROM stdin;
1	0001	12345-6	joao.silva@email.com	1
2	2564	25999874	2558996589	2
\.


--
-- TOC entry 5132 (class 0 OID 26093)
-- Dependencies: 237
-- Data for Name: topics; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.topics (cdtopic, nmtopic, nrorder) FROM stdin;
1	Do Objeto da Locação	1
2	CONTRATO PARTICULAR DE LOCAÇÃO DE IMÓVEL RESIDENCIAL	1
3	OBJETO DA LOCAÇÃO	2
4	DA VISTORIA DE ENTRADA	3
5	DO PRAZO	4
6	DO ALUGUEL	5
7	A DESTINAÇÃO DO IMÓVEL	6
8	DAS MANUTENÇÕES	7
9	DA SUBLOCAÇÃO	8
10	DAS BENFEITORIAS	9
11	DAS VEDAÇÕES	10
12	DAS DESPESAS COM CARTÓRIO	11
13	DA VISTORIA DE SAÍDA	12
14	DA GARANTIA LOCATÍCIA - CAUÇÃO	13
15	DOS ENCARGOS DA LOCAÇÃO	14
16	DOS DADOS DE CONTATO E COMUNICAÇÕES	15
17	OBSERVAÇÕES FINAIS E ASSINATURAS	16
\.


--
-- TOC entry 5134 (class 0 OID 26100)
-- Dependencies: 239
-- Data for Name: clauses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.clauses (cdclause, dstext, cdtopic, nrorder) FROM stdin;
1	O locador cede ao locatário o imóvel nas condições atuais.	1	1
2	Parágrafo Único: É permitida a permanência de animais de estimação, desde que não causem danos, barulho excessivo ou incômodos a terceiros, ficando os LOCATÁRIOS responsáveis por eventuais prejuízos.	7	2
3	É facultado ao LOCADOR ou seu representante recusar o recebimento das chaves, caso o imóvel objeto da locação, não esteja em perfeitas condições, exatamente como fora entregue aos LOCATÁRIOS no início da locação. Ocorrendo dita hipótese, ou seja, havendo irregularidades, continuará por conta dos LOCATÁRIOS e seus garantes, os aluguéis e demais encargos locatícios, até que o imóvel seja restituído ao LOCADOR, nas mesmas condições do início da locação.	17	1
4	Parágrafo Quinto: Caso o imóvel esteja sem fornecimento de energia elétrica, os LOCATÁRIOS deverão providenciar a ligação ou religação junto à CELESC, não sendo tal fato motivo para rescisão contratual. Débitos anteriores em nome do LOCADOR, se existentes, poderão ser pagos pelos LOCATÁRIOS mediante posterior reembolso.	4	7
5	Parágrafo Segundo: A vistoria de saída será realizada pelo LOCADOR tendo como base a vistoria de entrada e será utilizado o método comparativo, tanto de escrita como fotografia.	13	3
6	Parágrafo Quarto: Os LOCATÁRIOS declaram ter vistoriado previamente o imóvel, aceitando-o sem ressalvas.	4	6
7	Parágrafo Oitavo: O LOCADOR, sempre que entender conveniente ou necessário, poderá vistoriar o imóvel, avisando os LOCATÁRIOS com antecedência mínima de 72 (setenta e duas) horas.	4	10
8	Parágrafo Terceiro: A solicitação da vistoria de saída deverá ser feita com no mínimo 72 (setenta e duas) horas de antecedência.	13	4
9	Os LOCATÁRIOS terão o prazo de 10 (dez) dias, contados do recebimento das chaves, para apresentar por escrito eventuais divergências em relação à vistoria inicial.	4	2
10	Parágrafo Primeiro: o contrato poderá ser prorrogado mediante manifestação expressa das partes.	5	2
11	Casa localizada na rua %rua_imovel%, nº %numero_imovel%, bairro %bairro_imovel%, neste município de Joinville, com área área privativa de %area_privativa% m² e área comum de %area_comum% m², matrícula sob o nº %matricula_imovel%.	3	2
12	CLÁUSULA DÉCIMA QUINTA: Ficam a cargo exclusivo dos LOCATÁRIOS o pagamento das despesas de consumo de água e energia elétrica que devem manter as respectivas contas em seu nome e integralmente quitadas, devendo apresentá-las ao LOCADOR sempre que solicitado.	15	1
13	A caução não poderá ser utilizada como pagamento de aluguéis, encargos ou quaisquer obrigações durante a vigência do contrato.	14	3
14	Parágrafo Sexto: Após a entrega das chaves aos LOCATÁRIOS, recomenda-se que seja mudado o segredo das chaves, principalmente das portas de acesso ao imóvel, não respondendo o LOCADOR por quaisquer furtos ou roubos de bens que porventura acontecerem.	4	8
15	Assinaturas: LOCADOR: %nome_locador_assinatura%, Representante: %nome_filho_representante%, LOCATÁRIOS: %nome_locatario_assinatura%, TESTEMUNHAS: %nome_testemunha_1%, %nome_testemunha_2%	17	4
16	Parágrafo Primeiro: Objetivando o cumprimento do disposto no “caput” desta cláusula, os LOCATÁRIOS obrigam-se a bem conservar o imóvel para entregá-lo limpo e plenas condições de imediato uso de acordo com o Termo de Vistoria de Ocupação. Se assim não fizerem, o LOCADOR deve fazê-lo e cobrar os custos dos LOCATÁRIOS mediante a exibição dos recibos de execução das obras e pagamentos efetuados, os quais deverão estar acompanhados de, no mínimo, 02 (dois) orçamentos, devendo sempre ser exigido o de menor valor.	13	2
17	CLÁUSULA DÉCIMA: Correrão por conta dos LOCATÁRIOS as despesas de registro do presente contrato no Cartório de Registro de Títulos e Documentos e/ou Cartório Registro de Imóveis e reconhecimento de firma no cartório.	12	1
18	CLÁUSULA NONA: Não é permitido depositar materiais inflamáveis, explosivos ou corrosivos, nem cortar ou danificar árvores por acaso existentes, ficando os LOCATÁRIOS responsáveis pelos danos que causarem.	11	1
19	Parágrafo Terceiro: As partes estão cientes de que a cidade de Joinville (SC) sofre com longos períodos de chuva e alto índice de umidade, portanto cabe aos LOCATÁRIOS adotar os cuidados necessários para evitar danos decorrentes dessas condições climáticas, não sendo o LOCADOR responsável por prejuízos decorrentes do clima.	4	5
20	LOCATÁRIO: E-mail: %email_locatario% Telefone/WhatsApp: %telefone_locatario%	16	3
21	LOCATÁRIOS: %nome_locatario%, brasileira, casada, inscrito no CPF. sob o n° %cpf_locatario%, portador de identidade n° %identidade_locatario%, Órgão emissor %orgao_emissor_locatario%.	2	2
22	Caso haja débitos ou danos, o LOCADOR poderá reter total ou parcialmente o valor da caução para compensação, mediante apresentação dos comprovantes correspondentes.	14	2
23	CLÁUSULA TERCEIRA: O prazo de locação é de %prazo_meses% meses, iniciando em %data_inicio% e terminando, de pleno direito, no dia %data_termino%, independentemente de notificação.	5	1
24	Parágrafo Sétimo: Os LOCATÁRIOS deverão informar ao LOCADOR, no prazo máximo de 05 (cinco) dias, qualquer defeito constatado, sob pena de concordância tácita, ressalvados os vícios ocultos.	4	9
25	CLÁUSULA DÉCIMA QUARTA: Os LOCATÁRIOS prestam caução no valor de 01 (um) aluguel, totalizando %valor_caucao%, a ser devolvida em até 30 dias após a vistoria final, inexistindo pendências.	14	1
26	Parágrafo Primeiro: o consumo de água e energia elétrica será de responsabilidade exclusiva dos LOCATÁRIOS.	6	2
27	Parágrafo Segundo: na renovação o valor do aluguel deve ser ajustado de acordo com o índice %index% acumulado do período.	5	3
28	Parágrafo Segundo: O aluguel vence no dia 01 (um) de cada mês, sendo o primeiro pagamento para %primeiro_vencimento%, e deverão ser pagos por meio de transferência bancária para a seguinte conta: Banco: %banco% Agência: %agencia% Conta: %conta% Titular: %titular_conta% CPF: %cpf_titular%	6	3
29	CLÁUSULA DÉCIMA SEXTA: Para todos os fins deste contrato, as partes informam como válidos os seguintes dados de contato, comprometendo-se a comunicar qualquer alteração:	16	1
30	LOCADOR: %locador%, brasileiro, aposentado, inscrito no CPF sob o nº %cpf%, portador de identidade nº %identidade%, Órgão emissor %orgao_emissor%, separado. Representado por seu filho %filho%, brasileiro, %estado_civil_e_profissao_filho%, inscrito no CPF nº %cpf_filho%, portador da identidade nº %identidade_filho%, conforme Procuração Pública lavrada no %numero_tabelionato%º Tabelionato de Notas de %cidade tabelionato%, Livro %livro_tabelionato%, Folha %folha_livro_tabelionato%, datada de %data_tabelionato%	2	1
31	CLÁUSULA SÉTIMA: É vedada a sublocação ou cessão do imóvel sem autorização expressa do LOCADOR.	9	1
32	CLÁUSULA SEGUNDA: Os LOCATÁRIOS declaram que recebem o imóvel em perfeito estado de conservação e uso, conforme descrito no Termo de Vistoria de Entrada, que integra o presente contrato, obrigando-se a devolvê-lo no mesmo estado, ressalvadas apenas as deteriorações decorrentes do uso normal.	4	1
33	Parágrafo Quarto: A entrega das chaves para vistoria, quando da desocupação do imóvel, não exonera os LOCATÁRIOS das obrigações ora assumidas. NO CASO DO IMÓVEL NECESSITAR DE ALGUM REPARO, OS LOCATÁRIOS DEVERÃO PAGAR O ALUGUEL E ENCARGOS ATÉ A CONCLUSÃO DOS REPAROS E RESCISÃO DEFINITIVA DA LOCAÇÃO.	13	5
34	CLÁUSULA SEXTA: Os LOCATÁRIOS deverão devolver o imóvel locado, assim como o recebem. Fica ajustado entre as partes contratantes que os LOCATÁRIOS se comprometem a informar o LOCADOR qualquer ocorrência de vazamentos, infiltração, etc., que possa danificar a propriedade locada.	8	1
35	CLÁUSULA QUINTA: O imóvel destina-se exclusivamente a uso residencial, sendo vedada qualquer outra destinação.	7	1
36	CLÁUSULA QUARTA: O valor mensal do aluguel do imóvel, objeto desse contrato é de %valor_aluguel%.	6	1
37	Parágrafo Primeiro: O laudo de vistoria tem como finalidade exclusiva registrar o estado do imóvel no momento da entrega e subsidiar a comparação quando da devolução, não obrigando o LOCADOR a reparar os vícios nele apontados.	4	3
38	CLÁUSULA OITAVA: Nenhuma benfeitoria poderá ser realizada sem autorização escrita do LOCADOR, não sendo devida qualquer indenização.	10	1
39	LOCADOR: E-mail: %email_locador% Telefone/WhatsApp: %telefone_locador%	16	2
40	Parágrafo Primeiro: Os danos causados por LOCATÁRIOS, familiares ou visitantes deverão ser reparados às suas expensas.	8	2
41	Dá o referido imóvel e seus acessórios em locação aos LOCATÁRIOS, nas condições abaixo estabelecidas:	3	3
42	Parágrafo Terceiro: O atraso no pagamento sujeitará os LOCATÁRIOS à multa de %multa_atraso% e juros de %juros_mensal% ao mês.	6	4
43	CLÁUSULA DÉCIMA PRIMEIRA: Findo o prazo da locação, ora estabelecido, ou rescindido a locação, por qualquer motivo, será o imóvel restituído ao LOCADOR, em condições de ser imediatamente habitado com as quitações de energia e água.	13	1
44	Parágrafo Terceiro: a devolução antecipada do imóvel implicará multa equivalente a 02 (dois) meses de aluguel, calculada proporcionalmente ao período restante do contrato, nos termos do art. 4º da Lei nº 8.245/91.	5	4
45	CLÁUSULA PRIMEIRA: O LOCADOR, na condição de proprietário e possuidor de um:	3	1
46	E por estarem justos e contratados, assinam le presente instrumento em 02 (duas) vias de igual teor e forma, na presença de 02 (duas) testemunhas abaixo assinadas ou de forma digital com fundamento no artigo 740 § 4º do C.P.C, nos títulos executivos constituídos ou atestados por meio eletrônico, é admitida qualquer modalidade de assinatura eletrônica prevista em lei, dispensada a assinatura de testemunhas, para que produza os seus jurídicos e legais efeitos.	17	2
47	Joinville, %dia_assinatura% de %mes_assinatura% de %ano_assinatura%.	17	3
48	Parágrafo Segundo: O Termo de Vistoria poderá ser enviado por meio eletrônico, contendo fotos e descrições, sendo vedada qualquer alteração posterior.	4	4
49	Parágrafo Quinto: Caso o imóvel locado, seja abandonado pelos LOCATÁRIOS, sem que estes tenham promovido a respectiva entrega das chaves, de modo a evitar a deterioração do imóvel e eventual agravamento de possível inadimplência das obrigações contratuais, fica facultado ao LOCADOR promover a retomada da posse do imóvel locado, o que poderá ser realizado por meio de ato acompanhado por duas testemunhas ou por meio de ata notarial. A data da retomada da posse do imóvel em caso de abandono, que será acompanhada da vistoria final visando identificar o estado material do imóvel, será anunciada aos LOCATÁRIOS por meio do contato apontado neste contrato.	13	6
\.


--
-- TOC entry 5153 (class 0 OID 26173)
-- Dependencies: 258
-- Data for Name: contract_templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contract_templates (cdtemplate, nmtemplate, dsversion, fgactive) FROM stdin;
1	Contrato de Locação Padrão	1.0	t
\.


--
-- TOC entry 5156 (class 0 OID 26187)
-- Dependencies: 261
-- Data for Name: indexes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.indexes (cdindex, nmindex) FROM stdin;
1	IPCA
2	IPCA
\.


--
-- TOC entry 5162 (class 0 OID 26208)
-- Dependencies: 267
-- Data for Name: notaries; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notaries (cdnotary, cdcity, book, leaf, dt, nrnotary) FROM stdin;
1	1	Livro de Registros A	Folha 101	2026-04-20	\N
2	1	1	1	2026-04-25	\N
3	1	1	1	2026-04-25	1
\.


--
-- TOC entry 5147 (class 0 OID 26152)
-- Dependencies: 252
-- Data for Name: property_purposes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.property_purposes (cdpurpose, nmpurpose) FROM stdin;
1	Residencial
\.


--
-- TOC entry 5149 (class 0 OID 26159)
-- Dependencies: 254
-- Data for Name: property_status; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.property_status (cdstatus, nmstatus) FROM stdin;
1	Alugado
2	Disponível
\.


--
-- TOC entry 5145 (class 0 OID 26145)
-- Dependencies: 250
-- Data for Name: property_types; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.property_types (cdtype, nmtype) FROM stdin;
1	Apartamento
\.


--
-- TOC entry 5141 (class 0 OID 26129)
-- Dependencies: 246
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.properties (cdproperty, nrregistration, dsdescription, vltotalarea, cdaddress, cdtype, cdpurpose, cdstatus) FROM stdin;
1	MAT-99887	Apto com 2 quartos no centro	65.50	1	1	1	1
\.


--
-- TOC entry 5130 (class 0 OID 26086)
-- Dependencies: 235
-- Data for Name: contracts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contracts (cdcontract, dtcreation, dstitle, cdtemplate, cdproperty, cdindex, dtlimit, cdstatus, cdnotary) FROM stdin;
8	2026-04-25	Titulinho	1	1	\N	2026-04-25	2	\N
9	2025-04-25	nominio	1	1	1	2026-04-25	1	\N
10	2025-04-25	[JOB1-A] Locacao - Parcela Vence Em 7 Dias	1	1	1	2026-10-25	1	\N
11	2025-04-25	[JOB1-B] Locacao - Parcela Vence Hoje	1	1	1	2026-10-25	1	\N
12	2026-01-01	[JOB2-A] Locacao - Pagamento Registrado Hoje ContratoA	1	1	1	2027-01-01	1	\N
13	2026-02-01	[JOB2-B] Locacao - Pagamento Registrado Hoje ContratoB	1	1	1	2027-02-01	1	\N
14	2025-04-10	[JOB3-A] Locacao - Aniversario 1Ano Em Abril 2026	1	1	1	2027-04-10	1	\N
15	2024-04-20	[JOB3-B] Locacao - Aniversario 2Anos Em Abril 2026	1	1	1	2027-04-20	1	\N
16	2025-04-28	[JOB4-A] Locacao - Contrato Vence Em 3 Dias	1	1	1	2026-04-28	1	\N
17	2025-04-25	[JOB4-B] Locacao - Contrato Vence Hoje	1	1	1	2026-04-25	1	\N
18	2026-04-30	asidsdas	1	1	1	2027-05-23	1	2
19	2026-04-25	sidjasidjasd	1	1	1	2026-12-03	1	3
\.


--
-- TOC entry 5158 (class 0 OID 26194)
-- Dependencies: 263
-- Data for Name: index_rates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.index_rates (cdrate, refmonth, refyear, vlrate, cdindex) FROM stdin;
26	4	2024	0.0038	1
27	5	2024	0.0046	1
28	6	2024	0.0021	1
29	7	2024	0.0038	1
30	8	2024	-0.0002	1
31	9	2024	0.0044	1
32	10	2024	0.0056	1
33	11	2024	0.0039	1
34	12	2024	0.0052	1
35	1	2025	0.0016	1
36	2	2025	0.0131	1
37	3	2025	0.0056	1
38	4	2025	0.0043	1
39	5	2025	0.0026	1
40	6	2025	0.0024	1
41	7	2025	0.0026	1
42	8	2025	-0.0011	1
43	9	2025	0.0048	1
44	10	2025	0.0009	1
45	11	2025	0.0018	1
46	12	2025	0.0033	1
47	1	2026	0.0033	1
48	2	2026	0.0070	1
49	3	2026	0.0088	1
\.


--
-- TOC entry 5160 (class 0 OID 26201)
-- Dependencies: 265
-- Data for Name: installments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.installments (cdinstallment, dtdue, vlbase, vladjusted, cdstatus, dtpayment, nrinstallment, cdcontract, vlpenalty, vlinterest) FROM stdin;
32	2025-06-01	1200.00	\N	1	\N	2	9	10.00	1.00
33	2025-07-01	1200.00	\N	1	\N	3	9	10.00	1.00
34	2025-08-01	1200.00	\N	1	\N	4	9	10.00	1.00
35	2025-09-01	1200.00	\N	1	\N	5	9	10.00	1.00
36	2025-10-01	1200.00	\N	1	\N	6	9	10.00	1.00
37	2025-11-01	1200.00	\N	1	\N	7	9	10.00	1.00
38	2025-12-01	1200.00	\N	1	\N	8	9	10.00	1.00
39	2026-01-01	1200.00	\N	1	\N	9	9	10.00	1.00
40	2026-02-01	1200.00	\N	1	\N	10	9	10.00	1.00
41	2026-03-01	1200.00	\N	1	\N	11	9	10.00	1.00
43	2026-05-01	1301.48	\N	1	\N	13	9	0.02	0.00
44	2026-06-01	1301.48	\N	1	\N	14	9	0.02	0.00
45	2026-07-01	1301.48	\N	1	\N	15	9	0.02	0.00
46	2026-08-01	1301.48	\N	1	\N	16	9	0.02	0.00
47	2026-09-01	1301.48	\N	1	\N	17	9	0.02	0.00
48	2026-10-01	1301.48	\N	1	\N	18	9	0.02	0.00
49	2026-11-01	1301.48	\N	1	\N	19	9	0.02	0.00
50	2026-12-01	1301.48	\N	1	\N	20	9	0.02	0.00
51	2027-01-01	1301.48	\N	1	\N	21	9	0.02	0.00
52	2027-02-01	1301.48	\N	1	\N	22	9	0.02	0.00
53	2027-03-01	1301.48	\N	1	\N	23	9	0.02	0.00
54	2027-04-01	1301.48	\N	1	\N	24	9	0.02	0.00
31	2025-05-01	1200.00	0.00	2	2026-04-25	1	9	10.00	1.00
42	2026-04-28	1200.00	1249.71	1	\N	12	9	10.00	1.00
55	2026-05-02	1500.00	1500.00	1	\N	1	10	0.02	0.00
56	2026-04-25	1800.00	1800.00	1	\N	1	11	0.02	0.00
57	2026-04-10	1200.00	1200.00	2	2026-04-25	1	12	0.02	0.00
58	2026-04-15	2200.00	2200.00	2	2026-04-25	1	13	0.02	0.00
59	2026-05-10	1700.00	1700.00	1	\N	13	14	0.02	0.00
60	2026-05-20	1900.00	2050.00	1	\N	25	15	0.02	0.00
61	2026-04-28	1600.00	1600.00	1	\N	12	16	0.02	0.00
62	2026-04-25	2100.00	2100.00	1	\N	12	17	0.02	0.00
63	2026-05-25	3000.00	\N	1	\N	1	18	10.00	10.00
64	2026-06-25	3000.00	\N	1	\N	2	18	10.00	10.00
65	2026-07-25	3000.00	\N	1	\N	3	18	10.00	10.00
66	2026-08-25	3000.00	\N	1	\N	4	18	10.00	10.00
67	2026-09-25	3000.00	\N	1	\N	5	18	10.00	10.00
68	2026-10-25	3000.00	\N	1	\N	6	18	10.00	10.00
69	2026-11-25	3000.00	\N	1	\N	7	18	10.00	10.00
70	2026-12-25	3000.00	\N	1	\N	8	18	10.00	10.00
71	2027-01-25	3000.00	\N	1	\N	9	18	10.00	10.00
72	2027-02-25	3000.00	\N	1	\N	10	18	10.00	10.00
73	2027-03-25	3000.00	\N	1	\N	11	18	10.00	10.00
74	2027-04-25	3000.00	\N	1	\N	12	18	10.00	10.00
75	2026-05-25	2000.00	\N	1	\N	1	19	10.00	20.00
76	2026-06-25	2000.00	\N	1	\N	2	19	10.00	20.00
77	2026-07-25	2000.00	\N	1	\N	3	19	10.00	20.00
78	2026-08-25	2000.00	\N	1	\N	4	19	10.00	20.00
79	2026-09-25	2000.00	\N	1	\N	5	19	10.00	20.00
80	2026-10-25	2000.00	\N	1	\N	6	19	10.00	20.00
81	2026-11-25	2000.00	\N	1	\N	7	19	10.00	20.00
\.


--
-- TOC entry 5169 (class 0 OID 26409)
-- Dependencies: 274
-- Data for Name: notification_templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification_templates (cdnotificationtemplate, tpcode, nmtemplate, dsdescription) FROM stdin;
1	10	ALUGUEL_LEMBRETE_7D	Lembrete de vencimento de aluguel enviado 7 dias antes da data de vencimento da parcela
2	11	ALUGUEL_LEMBRETE_3D	Lembrete de vencimento de aluguel enviado 3 dias antes da data de vencimento da parcela
3	12	ALUGUEL_LEMBRETE_HOJE	Lembrete de vencimento de aluguel enviado no próprio dia do vencimento da parcela
4	20	ALUGUEL_CONFIRMACAO_PAGAMENTO	Aviso ao Locador confirmando que o pagamento da parcela foi registrado hoje
5	30	REAJUSTE_ANUAL	Aviso ao Locador e Locatário sobre o reajuste anual do valor do aluguel e o novo valor vigente
6	40	VENCIMENTO_CONTRATO_7D	Aviso de vencimento de contrato enviado 7 dias antes da data limite do contrato
7	41	VENCIMENTO_CONTRATO_3D	Aviso de vencimento de contrato enviado 3 dias antes da data limite do contrato
8	42	VENCIMENTO_CONTRATO_HOJE	Aviso de vencimento de contrato enviado no próprio dia do vencimento do contrato
\.


--
-- TOC entry 5164 (class 0 OID 26215)
-- Dependencies: 269
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notifications (cdnotification, dsmessage, dtsend, cdcontract, cduser, cdnotificationtemplate, fgchannel) FROM stdin;
2	[LEMBRETE] Aluguel do Contrato #10 vence em 02/05/2026 (R$ 1500,00). Por favor, efetue o pagamento.	2026-04-25	10	4	\N	1
3	[LEMBRETE] Aluguel do Contrato #16 vence em 28/04/2026 (R$ 1600,00). Por favor, efetue o pagamento.	2026-04-25	16	16	\N	1
4	[LEMBRETE] Aluguel do Contrato #11 vence em 25/04/2026 (R$ 1800,00). Por favor, efetue o pagamento.	2026-04-25	11	6	\N	1
5	[LEMBRETE] Aluguel do Contrato #17 vence em 25/04/2026 (R$ 2100,00). Por favor, efetue o pagamento.	2026-04-25	17	18	\N	1
6	[LEMBRETE] Aluguel do Contrato #10 vence em 02/05/2026 (R$ 1500,00). Por favor, efetue o pagamento.	2026-04-25	10	4	1	1
7	[LEMBRETE] Aluguel do Contrato #16 vence em 28/04/2026 (R$ 1600,00). Por favor, efetue o pagamento.	2026-04-25	16	16	2	1
8	[LEMBRETE] Aluguel do Contrato #11 vence em 25/04/2026 (R$ 1800,00). Por favor, efetue o pagamento.	2026-04-25	11	6	3	1
9	[LEMBRETE] Aluguel do Contrato #17 vence em 25/04/2026 (R$ 2100,00). Por favor, efetue o pagamento.	2026-04-25	17	18	3	1
10	[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #12 foi paga em 25/04/2026. Valor: R$ 1200,00.	2026-04-25	12	7	4	1
11	[PAGAMENTO CONFIRMADO] Parcela #1 do Contrato #13 foi paga em 25/04/2026. Valor: R$ 2200,00.	2026-04-25	13	9	4	1
\.


--
-- TOC entry 5165 (class 0 OID 26223)
-- Dependencies: 270
-- Data for Name: properties_users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.properties_users (cdproperty, cduser) FROM stdin;
1	1
\.


--
-- TOC entry 5167 (class 0 OID 26387)
-- Dependencies: 272
-- Data for Name: readjustment_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.readjustment_logs (cdlog, cdcontract, cdinstallment, cdindex, vlold, vlnew, dtreadjustment) FROM stdin;
25	9	42	1	1200.00	1249.71	2026-04-25
\.


--
-- TOC entry 5127 (class 0 OID 26071)
-- Dependencies: 232
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.roles (cdrole, nmrole) FROM stdin;
1	Locatário
2	Locador
3	Testemunha
4	Representante
\.


--
-- TOC entry 5154 (class 0 OID 26179)
-- Dependencies: 259
-- Data for Name: template_topics; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.template_topics (cdtopic, cdtemplate) FROM stdin;
1	1
2	1
3	1
4	1
5	1
6	1
7	1
8	1
9	1
10	1
11	1
12	1
13	1
14	1
15	1
16	1
17	1
\.


--
-- TOC entry 5128 (class 0 OID 26077)
-- Dependencies: 233
-- Data for Name: user_contract; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.user_contract (cdcontract, cduser, cdrole) FROM stdin;
10	4	1
10	3	2
11	6	1
11	5	2
12	8	1
12	7	2
13	10	1
13	9	2
14	12	1
14	11	2
15	14	1
15	13	2
16	16	1
16	15	2
17	18	1
17	17	2
18	1	2
18	2	1
19	1	2
19	2	1
19	3	3
19	4	3
\.


--
-- TOC entry 5175 (class 0 OID 0)
-- Dependencies: 221
-- Name: addresses_cdaddress_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.addresses_cdaddress_seq', 1, true);


--
-- TOC entry 5176 (class 0 OID 0)
-- Dependencies: 243
-- Name: bank_accounts_cdbankaccount_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.bank_accounts_cdbankaccount_seq', 2, true);


--
-- TOC entry 5177 (class 0 OID 0)
-- Dependencies: 227
-- Name: cities_cdcity_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cities_cdcity_seq', 1, true);


--
-- TOC entry 5178 (class 0 OID 0)
-- Dependencies: 238
-- Name: clauses_cdclause_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.clauses_cdclause_seq', 49, true);

--
-- TOC entry 5180 (class 0 OID 0)
-- Dependencies: 257
-- Name: contract_templates_cdtemplate_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contract_templates_cdtemplate_seq', 1, true);


--
-- TOC entry 5181 (class 0 OID 0)
-- Dependencies: 234
-- Name: contracts_cdcontract_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contracts_cdcontract_seq', 19, true);


--
-- TOC entry 5182 (class 0 OID 0)
-- Dependencies: 223
-- Name: countries_cdcountry_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.countries_cdcountry_seq', 1, true);


--
-- TOC entry 5183 (class 0 OID 0)
-- Dependencies: 229
-- Name: districts_cddistrict_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.districts_cddistrict_seq', 1, true);


--
-- TOC entry 5184 (class 0 OID 0)
-- Dependencies: 262
-- Name: index_rates_cdrate_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.index_rates_cdrate_seq', 49, true);


--
-- TOC entry 5185 (class 0 OID 0)
-- Dependencies: 260
-- Name: indexes_cdindex_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.indexes_cdindex_seq', 2, true);


--
-- TOC entry 5186 (class 0 OID 0)
-- Dependencies: 264
-- Name: installments_cdinstallment_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.installments_cdinstallment_seq', 81, true);


--
-- TOC entry 5187 (class 0 OID 0)
-- Dependencies: 266
-- Name: notaries_cdnotary_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notaries_cdnotary_seq', 3, true);


--
-- TOC entry 5188 (class 0 OID 0)
-- Dependencies: 273
-- Name: notification_templates_cdnotificationtemplate_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notification_templates_cdnotificationtemplate_seq', 8, true);


--
-- TOC entry 5189 (class 0 OID 0)
-- Dependencies: 268
-- Name: notifications_cdnotification_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notifications_cdnotification_seq', 11, true);


--
-- TOC entry 5190 (class 0 OID 0)
-- Dependencies: 255
-- Name: occupations_cdoccupation_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.occupations_cdoccupation_seq', 1, true);


--
-- TOC entry 5191 (class 0 OID 0)
-- Dependencies: 245
-- Name: properties_cdproperty_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.properties_cdproperty_seq', 1, true);


--
-- TOC entry 5192 (class 0 OID 0)
-- Dependencies: 251
-- Name: property_purposes_cdpurpose_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.property_purposes_cdpurpose_seq', 1, true);


--
-- TOC entry 5193 (class 0 OID 0)
-- Dependencies: 253
-- Name: property_status_cdstatus_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.property_status_cdstatus_seq', 2, true);


--
-- TOC entry 5194 (class 0 OID 0)
-- Dependencies: 249
-- Name: property_types_cdtype_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.property_types_cdtype_seq', 1, true);


--
-- TOC entry 5195 (class 0 OID 0)
-- Dependencies: 271
-- Name: readjustment_logs_cdlog_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.readjustment_logs_cdlog_seq', 25, true);


--
-- TOC entry 5196 (class 0 OID 0)
-- Dependencies: 231
-- Name: roles_cdrole_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_cdrole_seq', 4, true);


--
-- TOC entry 5197 (class 0 OID 0)
-- Dependencies: 225
-- Name: states_cdstate_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.states_cdstate_seq', 1, true);


--
-- TOC entry 5198 (class 0 OID 0)
-- Dependencies: 236
-- Name: topics_cdtopic_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.topics_cdtopic_seq', 17, true);


--
-- TOC entry 5199 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_cduser_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_cduser_seq', 18, true);


-- Completed on 2026-04-25 22:24:20

--
-- PostgreSQL database dump complete
--

\unrestrict ZsrMWNrQYDQ0GdUUz51ZGnCYS3LQbw0ErzvHJoImGt2gajYffBtU3PXYyhEBCOK

