package com.skysoftlk.vpnapp.Utils;

public class Constants {
    public static String FREE_SERVERS = "";
    public static String PREMIUM_SERVERS = "";
    public static String ONECONNECT_KEY = "";

    // MANUAL SERVER LIST FOR CHINA RESILIENCE
    // You can add more servers to this JSON string.
    public static final String MANUAL_SERVERS_JSON = "[" +
            "  {" +
            "    \"serverName\": \"India (Mumbai) Optimized\"," +
            "    \"flagUrl\": \"https://flagpedia.net/data/flags/w580/in.png\"," +
            "    \"ovpnConfig\": \"client\\nserver-poll-timeout 4\\nnobind\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 443 tcp\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 1194 udp\\nremote 168.144.83.221 1194 udp\\ndev tun\\ndev-type tun\\nremote-cert-tls server\\ntls-version-min 1.2\\nreneg-sec 604800\\ntun-mtu 1420\\nauth-user-pass\\nverb 3\\npush-peer-info\\ncipher AES-256-CBC\\n<ca>\\n-----BEGIN CERTIFICATE-----\\nMIIBdzCB/6ADAgECAgRqAw0eMAoGCCqGSM49BAMCMBUxEzARBgNVBAMMCk9wZW5W\\nUE4gQ0EwHhcNMjYwNTExMTEyMTAyWhcNMzYwNTA5MTEyMTAyWjAVMRMwEQYDVQQD\\nDApPcGVuVlBOIENBMHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE3FlN7apoH4/6SLae\\ncQEdMVXTfhzlreV8G7v2AKQNeZBLaeNmo/4QYSMrtMOLkK7oPoQQWLpJS5Qn7BIe\\nFCh057m7jbj/Hl8L7GbYyKDpInnOAynl+ugd9pkqTKHFALNXoyAwHjAPBgNVHRMB\\nAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAKBggqhkjOPQQDAgNnADBkAjAju8kZGqj9\\n8f/7p1/ugZJchRwMDGuPpaBtJYVGU1xVIpzBimOLJqX7MNX4B8gMl+sCMDaRkYnq\\ncGPOpHMOB7LMSLfXQ26d7EFfPczl/2JrdAiiq+BavirLIjy7JQvuatu3dg==\\n-----END CERTIFICATE-----\\n</ca>\\n<cert>\\n-----BEGIN CERTIFICATE-----\\nMIIBnzCCASWgAwIBAgIIbpxlpynZR9wwCgYIKoZIzj0EAwIwFTETMBEGA1UEAwwK\\nT3BlblZQTiBDQTAeFw0yNjA1MTExMTMwNThaFw0zNjA1MDkxMTMwNThaMBIxEDAO\\nBgNVBAMMB29wZW52cG4wdjAQBgcqhkjOPQIBBgUrgQQAIgNiAASWyTlZtbXDniTn\\ncHjPUeYB0geCiFT6hCW7yxwwvdRNK856lh0yU9PbNwQSbIYltk0RGjWa5g7rFmgM\\ndMoqQLtVVAjKOvfSvfH28A86rdrSRhAC4xJpOCRG1iBujTUgIdGjRTBDMAwGA1Ud\\nEwEB/wQCMAAwCwYDVR0PBAQDAgeAMBMGA1UdJQQMMAoGCCsGAQUFBwMCMBEGCWCG\\nSAGG+EIBAQQEAwIHgDAKBggqhkjOPQQDAgNoADBlAjEA/nhmoMpcSD1oEdMRqoSq\\nC5U1INe0Ma3fm0K2woK/5d51kIj0rxFYSceJFuiGIXbNAjB8mMBTXrb1v+SkX9PB\\nW5LrumJJEWzvppVNQidTo/agjFNkydv2Ujp+D03zPCKpLbI=\\n-----END CERTIFICATE-----\\n</cert>\\n<key>\\n-----BEGIN PRIVATE KEY-----\\nMIG2AgEAMBAGByqGSM49AgEGBSuBBAAiBIGeMIGbAgEBBDDW4ppuEXmn/h1DtFyY\\nV5ZxjpDq74QzdjLdt+EYDrUlpm7ytpN4yRvxnVxqqAKhesWhZANiAASWyTlZtbXD\\nniTncHjPUeYB0geCiFT6hCW7yxwwvdRNK856lh0yU9PbNwQSbIYltk0RGjWa5g7r\\nFmgMdMoqQLtVVAjKOvfSvfH28A86rdrSRhAC4xJpOCRG1iBujTUgIdE=\\n-----END PRIVATE KEY-----\\n</key>\\n<tls-crypt-v2>\\n-----BEGIN OpenVPN tls-crypt-v2 client key-----\\neuXYdXl/E6maq4h5XlB33TdSVJV+8UuT7N+2vPUn6gfWOWH03viJOSmRP/axa1qx\\nEH6l2iE7rzwu+OYYjkin1hmW3I95tNLdRuIqzsNequWpxeT25YVi+xDMHSJdGMIh\\n3nywJFim28JIdXe85RPlVriHp+mWOfchzUGYqre8S2KrOV5Boj7oAHqXaYBWVGi+\\n8i9Pxl454jIoQ5919ozxSLi2a9jmXipj10hFGI6FTL4q2zAxjhxA9R7K8u6AnOm9\\nKCVdwSILemkoN+e5QqT1Y1P472U1uKtBCTqe0GrWDbPuhmPag4h3KAUqRI2fi+Ap\\ng/r0NH42mRaUgjJgh08CmmVD4/2bnNMPC2kCyxGPz8X51z3TY7IOSbymoQP7CWqD\\n0aQOMpJ7K8u7zgxnSDPCgM4feGu6eoyO85CutxbrJ7SY9SCtwKUwTxSucbBSOcZV\\nWCx7KvhjjYXiySETpMyEaX920cPUgis3swBhX3bCVREitAWzgh2BsbXrgNSBdM0u\\nEc+nP5Go+Wrs/uICKJuM99Qd3hiQe7SHGE86eRm774Pvb7JGgESyQV3o63QlpJrn\\nkzGs8sUHNQJB+eNHoO3NqIH1DyyvBQdefePXEiT07WzTcY+sKcmnKeePIlQqin3N\\ntfeFnQ00nnBto2wJ4xf4d1FkDWfaALTyeoUq0gNVEjJdQ5d8DzHClI2IDAh+k4iO\\nf5ev++aYGOrwlWBiQMfOrqkT+r1yhp2uxfsKEnTs7fQ7/dqUJiV7rioO5nNJbrrn\\nS+RqzlHAajNQqIL47aVNhREXOCgnw08BWQ==\\n-----END OpenVPN tls-crypt-v2 client key-----\\n</tls-crypt-v2>\"," +
            "    \"userName\": \"openvpn\"," +
            "    \"password\": \"openvpn\"" +
            "  }" +
            "]";
}
