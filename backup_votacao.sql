--
-- PostgreSQL database dump
--

\restrict thlOU6Y98KTfv99zOSXgPFthLePMhWs7nU3RZaC4KACHShCUhd39RQ2u0npjxp0

-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: associado; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.associado (
    id uuid NOT NULL,
    nome character varying(255),
    cpf character varying(11) NOT NULL
);


ALTER TABLE public.associado OWNER TO postgres;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: pauta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pauta (
    id uuid NOT NULL,
    titulo character varying(255) NOT NULL,
    descricao text,
    status character varying(20) DEFAULT 'CRIADA'::character varying NOT NULL,
    data_abertura timestamp with time zone,
    data_fechamento timestamp with time zone,
    criada_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT pauta_status_check CHECK (((status)::text = ANY ((ARRAY['CRIADA'::character varying, 'EM_VOTACAO'::character varying, 'ENCERRADA'::character varying])::text[])))
);


ALTER TABLE public.pauta OWNER TO postgres;

--
-- Name: voto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.voto (
    id uuid NOT NULL,
    pauta_id uuid NOT NULL,
    associado_id uuid NOT NULL,
    voto character varying(3) NOT NULL,
    data_voto timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT voto_voto_check CHECK (((voto)::text = ANY ((ARRAY['SIM'::character varying, 'NAO'::character varying])::text[])))
);


ALTER TABLE public.voto OWNER TO postgres;

--
-- Data for Name: associado; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.associado (id, nome, cpf) FROM stdin;
1a281bf4-5a6e-401d-b607-ae05cd1e0bbe	Bruno Xavier	64456307072
cbb97ef2-1617-4fc8-88cd-82dd53996075	Heitor Gustavo Roberto Pires	95634653770
92993aea-2e6b-468d-832b-c8c16862f76c	Oliver Luiz Erick da Cunha	50882408569
a850eb56-b59a-420b-a76d-8aac5e549d69	Ayla Isabelly Moura	83373823250
dc069b9f-5fbc-4ef1-82a9-26eb1a726d5c	Alana Sara Silvana da Costa	41164458515
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	create pauta	SQL	V1__create_pauta.sql	-297952900	postgres	2026-04-07 12:05:40.593955	12	t
2	2	create associado	SQL	V2__create_associado.sql	-1191074287	postgres	2026-04-07 12:05:40.619783	7	t
3	3	create voto	SQL	V3__create_voto.sql	1839927055	postgres	2026-04-07 12:05:40.635108	9	t
\.


--
-- Data for Name: pauta; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pauta (id, titulo, descricao, status, data_abertura, data_fechamento, criada_em) FROM stdin;
770b3c8f-e017-4fc3-91fe-87fc8ac3d412	Aprovação de orçamento	Votação para decidir o orçamento anual	ENCERRADA	2026-04-07 15:36:00.612+00	2026-04-07 15:53:41.461305+00	2026-04-07 15:24:59.594235+00
\.


--
-- Data for Name: voto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.voto (id, pauta_id, associado_id, voto, data_voto) FROM stdin;
6faab538-53ec-4245-b124-fc6293c0a832	770b3c8f-e017-4fc3-91fe-87fc8ac3d412	1a281bf4-5a6e-401d-b607-ae05cd1e0bbe	SIM	2026-04-07 12:36:14.053454
18da3932-8fc8-4e10-95ea-ae23f587fd9b	770b3c8f-e017-4fc3-91fe-87fc8ac3d412	cbb97ef2-1617-4fc8-88cd-82dd53996075	SIM	2026-04-07 12:36:49.538963
b01b2487-8c71-4a5e-8e3c-91078309130b	770b3c8f-e017-4fc3-91fe-87fc8ac3d412	92993aea-2e6b-468d-832b-c8c16862f76c	SIM	2026-04-07 12:41:59.974156
e6e92f1f-6c25-4483-8837-1b47620e1453	770b3c8f-e017-4fc3-91fe-87fc8ac3d412	a850eb56-b59a-420b-a76d-8aac5e549d69	NAO	2026-04-07 12:45:28.396422
fa8d0108-47dc-417a-ac7b-d3ea8a88502a	770b3c8f-e017-4fc3-91fe-87fc8ac3d412	dc069b9f-5fbc-4ef1-82a9-26eb1a726d5c	NAO	2026-04-07 12:45:44.332961
\.


--
-- Name: associado associado_cpf_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.associado
    ADD CONSTRAINT associado_cpf_key UNIQUE (cpf);


--
-- Name: associado associado_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.associado
    ADD CONSTRAINT associado_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: pauta pauta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pauta
    ADD CONSTRAINT pauta_pkey PRIMARY KEY (id);


--
-- Name: voto voto_pauta_id_associado_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voto
    ADD CONSTRAINT voto_pauta_id_associado_id_key UNIQUE (pauta_id, associado_id);


--
-- Name: voto voto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voto
    ADD CONSTRAINT voto_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_associado_cpf; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_associado_cpf ON public.associado USING btree (cpf);


--
-- Name: idx_pauta_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_pauta_status ON public.pauta USING btree (status);


--
-- Name: idx_voto_pauta_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_voto_pauta_id ON public.voto USING btree (pauta_id);


--
-- Name: voto voto_associado_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voto
    ADD CONSTRAINT voto_associado_id_fkey FOREIGN KEY (associado_id) REFERENCES public.associado(id);


--
-- Name: voto voto_pauta_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voto
    ADD CONSTRAINT voto_pauta_id_fkey FOREIGN KEY (pauta_id) REFERENCES public.pauta(id);


--
-- PostgreSQL database dump complete
--

\unrestrict thlOU6Y98KTfv99zOSXgPFthLePMhWs7nU3RZaC4KACHShCUhd39RQ2u0npjxp0

