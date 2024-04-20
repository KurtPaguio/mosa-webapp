PGDMP         	                |            mosa-webapp    13.11    13.11 .               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    120904    mosa-webapp    DATABASE     o   CREATE DATABASE "mosa-webapp" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'English_Philippines.1252';
    DROP DATABASE "mosa-webapp";
                postgres    false            �            1259    120913    account    TABLE     �  CREATE TABLE public.account (
    id character varying NOT NULL,
    date_created timestamp without time zone,
    full_name character varying(500),
    email character varying(500),
    contact_number character varying(500),
    address character varying(255),
    password character varying(500),
    user_role character varying(255),
    login_otp bigint DEFAULT 0,
    change_password_otp bigint DEFAULT 0,
    change_password_token character varying(500),
    is_ordering boolean DEFAULT false
);
    DROP TABLE public.account;
       public         heap    postgres    false            �            1259    121077    account_registration    TABLE     �  CREATE TABLE public.account_registration (
    id character varying NOT NULL,
    date_created timestamp without time zone,
    full_name character varying(500),
    email character varying(500),
    contact_number character varying(500),
    address character varying(255),
    password character varying(500),
    user_role character varying(255),
    register_otp bigint DEFAULT 0,
    status character varying(255)
);
 (   DROP TABLE public.account_registration;
       public         heap    postgres    false            �            1259    120929    accounts_roles    TABLE     �   CREATE TABLE public.accounts_roles (
    account_id character varying(500) NOT NULL,
    role_id character varying(500) NOT NULL
);
 "   DROP TABLE public.accounts_roles;
       public         heap    postgres    false            �            1259    121240    activity_logs    TABLE     �   CREATE TABLE public.activity_logs (
    id character varying NOT NULL,
    date_created timestamp without time zone,
    actor character varying(255),
    activity text,
    is_staff boolean
);
 !   DROP TABLE public.activity_logs;
       public         heap    postgres    false            �            1259    121334    brand    TABLE     �   CREATE TABLE public.brand (
    id character varying NOT NULL,
    date_created timestamp without time zone,
    name character varying,
    image_url text
);
    DROP TABLE public.brand;
       public         heap    postgres    false            �            1259    121834    cart    TABLE     �  CREATE TABLE public.cart (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    customer_id character varying NOT NULL,
    thread_type_id character varying NOT NULL,
    details_id character varying NOT NULL,
    quantity bigint NOT NULL,
    total_price double precision NOT NULL,
    is_checked_out boolean NOT NULL,
    is_paid boolean DEFAULT false NOT NULL,
    is_order_now boolean DEFAULT false
);
    DROP TABLE public.cart;
       public         heap    postgres    false            �            1259    121954    kiosk    TABLE     �  CREATE TABLE public.kiosk (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    token character varying NOT NULL,
    thread_type_id character varying NOT NULL,
    details_id character varying NOT NULL,
    quantity bigint NOT NULL,
    total_price double precision NOT NULL,
    is_checked_out boolean DEFAULT false NOT NULL,
    queueing_number bigint DEFAULT 1
);
    DROP TABLE public.kiosk;
       public         heap    postgres    false            �            1259    121965    onsite_order    TABLE     �  CREATE TABLE public.onsite_order (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    thread_type_id character varying NOT NULL,
    details_id character varying NOT NULL,
    quantity bigint NOT NULL,
    total_price double precision NOT NULL,
    is_paid boolean NOT NULL,
    is_being_ordered boolean DEFAULT false NOT NULL,
    admin_id character varying
);
     DROP TABLE public.onsite_order;
       public         heap    postgres    false            �            1259    121842    orders    TABLE     �  CREATE TABLE public.orders (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    order_type character varying NOT NULL,
    order_status character varying NOT NULL,
    reference_number character varying,
    payment_method character varying,
    cart_id character varying,
    kiosk_id character varying,
    onsite_order_id character varying,
    order_id character varying NOT NULL
);
    DROP TABLE public.orders;
       public         heap    postgres    false            �            1259    120921    role    TABLE     �   CREATE TABLE public.role (
    id character varying NOT NULL,
    name character varying(255),
    description character varying(500)
);
    DROP TABLE public.role;
       public         heap    postgres    false            �            1259    121223    schedule    TABLE     �   CREATE TABLE public.schedule (
    id character varying NOT NULL,
    date_created timestamp without time zone,
    ordered_by character varying,
    date_scheduled character varying,
    comments text,
    is_approved boolean
);
    DROP TABLE public.schedule;
       public         heap    postgres    false            �            1259    121342    thread_type    TABLE       CREATE TABLE public.thread_type (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    type character varying NOT NULL,
    rating integer DEFAULT 5,
    image_url text,
    description text,
    brand_id character varying
);
    DROP TABLE public.thread_type;
       public         heap    postgres    false            �            1259    121353    thread_type_details    TABLE     ~  CREATE TABLE public.thread_type_details (
    id character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    width character varying,
    aspect_ratio character varying,
    diameter character varying,
    sidewall character varying,
    ply_rating character varying,
    price double precision,
    stocks bigint,
    thread_type_id character varying
);
 '   DROP TABLE public.thread_type_details;
       public         heap    postgres    false            �            1259    120941    token_blacklist    TABLE     q   CREATE TABLE public.token_blacklist (
    id character varying NOT NULL,
    token character varying NOT NULL
);
 #   DROP TABLE public.token_blacklist;
       public         heap    postgres    false            
          0    120913    account 
   TABLE DATA           �   COPY public.account (id, date_created, full_name, email, contact_number, address, password, user_role, login_otp, change_password_otp, change_password_token, is_ordering) FROM stdin;
    public          postgres    false    200   �<                 0    121077    account_registration 
   TABLE DATA           �   COPY public.account_registration (id, date_created, full_name, email, contact_number, address, password, user_role, register_otp, status) FROM stdin;
    public          postgres    false    204   �=                 0    120929    accounts_roles 
   TABLE DATA           =   COPY public.accounts_roles (account_id, role_id) FROM stdin;
    public          postgres    false    202   �=                 0    121240    activity_logs 
   TABLE DATA           T   COPY public.activity_logs (id, date_created, actor, activity, is_staff) FROM stdin;
    public          postgres    false    206   >                 0    121334    brand 
   TABLE DATA           B   COPY public.brand (id, date_created, name, image_url) FROM stdin;
    public          postgres    false    207   �>                 0    121834    cart 
   TABLE DATA           �   COPY public.cart (id, date_created, customer_id, thread_type_id, details_id, quantity, total_price, is_checked_out, is_paid, is_order_now) FROM stdin;
    public          postgres    false    210   �>                 0    121954    kiosk 
   TABLE DATA           �   COPY public.kiosk (id, date_created, token, thread_type_id, details_id, quantity, total_price, is_checked_out, queueing_number) FROM stdin;
    public          postgres    false    212   �>                 0    121965    onsite_order 
   TABLE DATA           �   COPY public.onsite_order (id, date_created, thread_type_id, details_id, quantity, total_price, is_paid, is_being_ordered, admin_id) FROM stdin;
    public          postgres    false    213   �>                 0    121842    orders 
   TABLE DATA           �   COPY public.orders (id, date_created, order_type, order_status, reference_number, payment_method, cart_id, kiosk_id, onsite_order_id, order_id) FROM stdin;
    public          postgres    false    211   ?                 0    120921    role 
   TABLE DATA           5   COPY public.role (id, name, description) FROM stdin;
    public          postgres    false    201   +?                 0    121223    schedule 
   TABLE DATA           g   COPY public.schedule (id, date_created, ordered_by, date_scheduled, comments, is_approved) FROM stdin;
    public          postgres    false    205   �?                 0    121342    thread_type 
   TABLE DATA           g   COPY public.thread_type (id, date_created, type, rating, image_url, description, brand_id) FROM stdin;
    public          postgres    false    208   �?                 0    121353    thread_type_details 
   TABLE DATA           �   COPY public.thread_type_details (id, date_created, width, aspect_ratio, diameter, sidewall, ply_rating, price, stocks, thread_type_id) FROM stdin;
    public          postgres    false    209   @                 0    120941    token_blacklist 
   TABLE DATA           4   COPY public.token_blacklist (id, token) FROM stdin;
    public          postgres    false    203   2@       m           2606    120920    account account_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.account DROP CONSTRAINT account_pkey;
       public            postgres    false    200            u           2606    121084 .   account_registration account_registration_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY public.account_registration
    ADD CONSTRAINT account_registration_pkey PRIMARY KEY (id);
 X   ALTER TABLE ONLY public.account_registration DROP CONSTRAINT account_registration_pkey;
       public            postgres    false    204            q           2606    120936 "   accounts_roles accounts_roles_pkey 
   CONSTRAINT     q   ALTER TABLE ONLY public.accounts_roles
    ADD CONSTRAINT accounts_roles_pkey PRIMARY KEY (account_id, role_id);
 L   ALTER TABLE ONLY public.accounts_roles DROP CONSTRAINT accounts_roles_pkey;
       public            postgres    false    202    202            y           2606    121247     activity_logs activity_logs_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.activity_logs
    ADD CONSTRAINT activity_logs_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.activity_logs DROP CONSTRAINT activity_logs_pkey;
       public            postgres    false    206            {           2606    121341    brand brand_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.brand DROP CONSTRAINT brand_pkey;
       public            postgres    false    207            �           2606    121841    cart cart_pkey 
   CONSTRAINT     L   ALTER TABLE ONLY public.cart
    ADD CONSTRAINT cart_pkey PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.cart DROP CONSTRAINT cart_pkey;
       public            postgres    false    210            �           2606    121962    kiosk kiosk_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.kiosk
    ADD CONSTRAINT kiosk_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.kiosk DROP CONSTRAINT kiosk_pkey;
       public            postgres    false    212            �           2606    121972    onsite_order onsite_order_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.onsite_order
    ADD CONSTRAINT onsite_order_pkey PRIMARY KEY (id);
 H   ALTER TABLE ONLY public.onsite_order DROP CONSTRAINT onsite_order_pkey;
       public            postgres    false    213            �           2606    121940    orders orders_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
       public            postgres    false    211            o           2606    120928    role role_pkey 
   CONSTRAINT     L   ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.role DROP CONSTRAINT role_pkey;
       public            postgres    false    201            w           2606    121230    schedule schedule_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.schedule DROP CONSTRAINT schedule_pkey;
       public            postgres    false    205                       2606    121361 ,   thread_type_details thread_type_details_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public.thread_type_details
    ADD CONSTRAINT thread_type_details_pkey PRIMARY KEY (id);
 V   ALTER TABLE ONLY public.thread_type_details DROP CONSTRAINT thread_type_details_pkey;
       public            postgres    false    209            }           2606    121352    thread_type thread_type_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.thread_type
    ADD CONSTRAINT thread_type_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.thread_type DROP CONSTRAINT thread_type_pkey;
       public            postgres    false    208            s           2606    120948 3   token_blacklist token_account_blacklist_status_pkey 
   CONSTRAINT     q   ALTER TABLE ONLY public.token_blacklist
    ADD CONSTRAINT token_account_blacklist_status_pkey PRIMARY KEY (id);
 ]   ALTER TABLE ONLY public.token_blacklist DROP CONSTRAINT token_account_blacklist_status_pkey;
       public            postgres    false    203            
   �   x����0 �����(�$qA�	K!���*�(/�n��$ c�t�$��i�
��ϡ#��(��,\/",��	�V���F�Ѓx�'�Gn�A���\����i�BU��n��n�c���i]�K��G�e���ܨ=�rr^�,V�K"��/��$�ś4ɒ����"3�QZ�cY�Ee8�            x������ � �         q   x�ʱ!����<�$ԋR�%x����̒�:��	Ws���}3�q�-	c*�Jb]؛sI�>��.#2gah��R��e���zz�t��1�A3YL�hz�~Zk?�"&|         l   x�=�1�  �N��/M#��N��E,������w�>���r��YQ�Qk��C�i\�p���I�	�i�8���ֶ������|ں�^�5��@�w+_g�>x����            x������ � �            x������ � �            x������ � �            x������ � �            x������ � �         �   x�M�K�0 �5���a�� q�'w&fh�� i���B�G�$Hڀr��!����ɬ!�b����,Ͼ�[7S��;��6zq.��أw�j�@I��Z��	�4�f�n�%;��%�L�_E�KP*T���b��*�b��n>�1G�g�e��9�            x������ � �            x������ � �            x������ � �            x������ � �     