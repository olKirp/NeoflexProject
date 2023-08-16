--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3 (Debian 13.3-1.pgdg100+1)
-- Dumped by pg_dump version 13.3 (Debian 13.3-1.pgdg100+1)

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
-- Name: application; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.application (
    id bigint NOT NULL,
    applied_offer jsonb,
    creation_date date NOT NULL,
    ses_code character varying(255),
    sign_date date,
    status character varying(255) NOT NULL,
    status_history jsonb,
    client_id bigint NOT NULL,
    credit_id bigint,
    CONSTRAINT application_status_check CHECK (((status)::text = ANY ((ARRAY['PREAPPROVAL'::character varying, 'APPROVED'::character varying, 'CC_DENIED'::character varying, 'CC_APPROVED'::character varying, 'PREPARE_DOCUMENTS'::character varying, 'DOCUMENT_CREATED'::character varying, 'CLIENT_DENIED'::character varying, 'DOCUMENT_SIGNED'::character varying, 'CREDIT_ISSUED'::character varying])::text[])))
);


ALTER TABLE public.application OWNER TO "user";

--
-- Name: application_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.application_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.application_id_seq OWNER TO "user";

--
-- Name: application_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user
--

ALTER SEQUENCE public.application_id_seq OWNED BY public.application.id;


--
-- Name: client; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.client (
    id bigint NOT NULL,
    account character varying(255),
    birth_date date NOT NULL,
    dependent_amount integer,
    email character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    gender character varying(255),
    last_name character varying(255) NOT NULL,
    marital_status character varying(255),
    middle_name character varying(255),
    employment_id bigint,
    passport_id bigint,
    CONSTRAINT client_gender_check CHECK (((gender)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'NON_BINARY'::character varying])::text[]))),
    CONSTRAINT client_marital_status_check CHECK (((marital_status)::text = ANY ((ARRAY['MARRIED'::character varying, 'SINGLE'::character varying, 'DIVORCED'::character varying, 'WIDOW_WIDOWER'::character varying])::text[])))
);


ALTER TABLE public.client OWNER TO "user";

--
-- Name: client_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.client_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.client_id_seq OWNER TO "user";

--
-- Name: client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user
--

ALTER SEQUENCE public.client_id_seq OWNED BY public.client.id;


--
-- Name: credit; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.credit (
    id bigint NOT NULL,
    amount numeric(38,2) NOT NULL,
    credit_status character varying(255) NOT NULL,
    insurance_enabled boolean NOT NULL,
    salary_client boolean NOT NULL,
    monthly_payment numeric(38,2) NOT NULL,
    payment_schedule jsonb NOT NULL,
    psk numeric(38,2) NOT NULL,
    rate numeric(38,2) NOT NULL,
    term integer NOT NULL,
    CONSTRAINT credit_amount_check CHECK ((amount >= (0)::numeric)),
    CONSTRAINT credit_credit_status_check CHECK (((credit_status)::text = ANY ((ARRAY['CALCULATED'::character varying, 'ISSUED'::character varying])::text[]))),
    CONSTRAINT credit_monthly_payment_check CHECK ((monthly_payment >= (0)::numeric)),
    CONSTRAINT credit_psk_check CHECK ((psk >= (0)::numeric)),
    CONSTRAINT credit_rate_check CHECK ((rate >= (0)::numeric)),
    CONSTRAINT credit_term_check CHECK ((term >= 0))
);


ALTER TABLE public.credit OWNER TO "user";

--
-- Name: credit_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.credit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.credit_id_seq OWNER TO "user";

--
-- Name: credit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user
--

ALTER SEQUENCE public.credit_id_seq OWNED BY public.credit.id;


--
-- Name: employment; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.employment (
    id bigint NOT NULL,
    employer_inn character varying(12),
    "position" character varying(255),
    salary numeric(38,2),
    status character varying(255),
    work_experience_current integer,
    work_experience_total integer,
    CONSTRAINT employment_position_check CHECK ((("position")::text = ANY ((ARRAY['WORKER'::character varying, 'MID_MANAGER'::character varying, 'TOP_MANAGER'::character varying, 'OWNER'::character varying])::text[]))),
    CONSTRAINT employment_salary_check CHECK ((salary >= (0)::numeric)),
    CONSTRAINT employment_status_check CHECK (((status)::text = ANY ((ARRAY['UNEMPLOYED'::character varying, 'EMPLOYED'::character varying, 'SELF_EMPLOYED'::character varying, 'BUSINESS_OWNER'::character varying])::text[]))),
    CONSTRAINT employment_work_experience_current_check CHECK ((work_experience_current >= 0)),
    CONSTRAINT employment_work_experience_total_check CHECK ((work_experience_total >= 0))
);


ALTER TABLE public.employment OWNER TO "user";

--
-- Name: employment_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.employment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employment_id_seq OWNER TO "user";

--
-- Name: employment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user
--

ALTER SEQUENCE public.employment_id_seq OWNED BY public.employment.id;


--
-- Name: passport; Type: TABLE; Schema: public; Owner: user
--

CREATE TABLE public.passport (
    id bigint NOT NULL,
    issue_branch character varying(255),
    issue_date date,
    number character varying(6) NOT NULL,
    series character varying(4) NOT NULL
);


ALTER TABLE public.passport OWNER TO "user";

--
-- Name: passport_id_seq; Type: SEQUENCE; Schema: public; Owner: user
--

CREATE SEQUENCE public.passport_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.passport_id_seq OWNER TO "user";

--
-- Name: passport_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user
--

ALTER SEQUENCE public.passport_id_seq OWNED BY public.passport.id;


--
-- Name: application id; Type: DEFAULT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application ALTER COLUMN id SET DEFAULT nextval('public.application_id_seq'::regclass);


--
-- Name: client id; Type: DEFAULT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client ALTER COLUMN id SET DEFAULT nextval('public.client_id_seq'::regclass);


--
-- Name: credit id; Type: DEFAULT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.credit ALTER COLUMN id SET DEFAULT nextval('public.credit_id_seq'::regclass);


--
-- Name: employment id; Type: DEFAULT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.employment ALTER COLUMN id SET DEFAULT nextval('public.employment_id_seq'::regclass);


--
-- Name: passport id; Type: DEFAULT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.passport ALTER COLUMN id SET DEFAULT nextval('public.passport_id_seq'::regclass);


--
-- Data for Name: application; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.application (id, applied_offer, creation_date, ses_code, sign_date, status, status_history, client_id, credit_id) FROM stdin;
\.


--
-- Data for Name: client; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.client (id, account, birth_date, dependent_amount, email, first_name, gender, last_name, marital_status, middle_name, employment_id, passport_id) FROM stdin;
\.


--
-- Data for Name: credit; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.credit (id, amount, credit_status, insurance_enabled, salary_client, monthly_payment, payment_schedule, psk, rate, term) FROM stdin;
\.


--
-- Data for Name: employment; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.employment (id, employer_inn, "position", salary, status, work_experience_current, work_experience_total) FROM stdin;
\.


--
-- Data for Name: passport; Type: TABLE DATA; Schema: public; Owner: user
--

COPY public.passport (id, issue_branch, issue_date, number, series) FROM stdin;
\.


--
-- Name: application_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.application_id_seq', 1, false);


--
-- Name: client_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.client_id_seq', 1, false);


--
-- Name: credit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.credit_id_seq', 1, false);


--
-- Name: employment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.employment_id_seq', 1, false);


--
-- Name: passport_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user
--

SELECT pg_catalog.setval('public.passport_id_seq', 1, false);


--
-- Name: application application_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application
    ADD CONSTRAINT application_pkey PRIMARY KEY (id);


--
-- Name: client client_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT client_pkey PRIMARY KEY (id);


--
-- Name: credit credit_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.credit
    ADD CONSTRAINT credit_pkey PRIMARY KEY (id);


--
-- Name: employment employment_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.employment
    ADD CONSTRAINT employment_pkey PRIMARY KEY (id);


--
-- Name: passport passport_pkey; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.passport
    ADD CONSTRAINT passport_pkey PRIMARY KEY (id);


--
-- Name: client uk_495g35l73whlk1fdkvw2n6rkm; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT uk_495g35l73whlk1fdkvw2n6rkm UNIQUE (passport_id);


--
-- Name: application uk_6e90jvkn0xacoatqyje680hyn; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application
    ADD CONSTRAINT uk_6e90jvkn0xacoatqyje680hyn UNIQUE (client_id);


--
-- Name: client uk_d23ocnqblqbapmdh78hspdjws; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT uk_d23ocnqblqbapmdh78hspdjws UNIQUE (employment_id);


--
-- Name: application uk_r7531yxb97ji5am6qpixnaux5; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application
    ADD CONSTRAINT uk_r7531yxb97ji5am6qpixnaux5 UNIQUE (credit_id);


--
-- Name: client unique_account; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT unique_account UNIQUE (account);


--
-- Name: passport unique_passport_series_and_number; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.passport
    ADD CONSTRAINT unique_passport_series_and_number UNIQUE (series, number);


--
-- Name: employment uniqueinn; Type: CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.employment
    ADD CONSTRAINT uniqueinn UNIQUE (employer_inn);


--
-- Name: application fk_application_client; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application
    ADD CONSTRAINT fk_application_client FOREIGN KEY (client_id) REFERENCES public.client(id);


--
-- Name: application fk_application_credit; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.application
    ADD CONSTRAINT fk_application_credit FOREIGN KEY (credit_id) REFERENCES public.credit(id);


--
-- Name: client fk_client_employment; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT fk_client_employment FOREIGN KEY (employment_id) REFERENCES public.employment(id);


--
-- Name: client fk_client_passport; Type: FK CONSTRAINT; Schema: public; Owner: user
--

ALTER TABLE ONLY public.client
    ADD CONSTRAINT fk_client_passport FOREIGN KEY (passport_id) REFERENCES public.passport(id);


--
-- PostgreSQL database dump complete
--

