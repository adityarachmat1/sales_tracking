package com.user.salestracking.Api_Service;

public class Api_url {
    private static final String URL = "http://192.168.80.2/";

    public static final String URL_REGISTER = URL + "sales_tracking/user/Api.php?apicall=register";
    public static final String URL_LOGIN= URL + "sales_tracking/user/Api.php?apicall=login";
    public static final String URL_INSERT_CALL= URL + "sales_tracking/post/insertdata.php?api=call";
    public static final String URL_INSERT_VISIT= URL + "sales_tracking/post/insertdata.php?api=visit";
    public static final String GET_DATA_DONATUR= URL + "sales_tracking/getDataDonatur/dataDonatur.php";
}
