package com.user.salestracking.Api_Service;

public class Api_url {
    private static final String URL = "http://192.168.1.28/";

    // Db User
    public static final String URL_REGISTER = URL + "sales_tracking/user/Api.php?apicall=register";
    public static final String URL_LOGIN= URL + "sales_tracking/user/Api.php?apicall=login";
    public static final String URL_EDIT_DONATUR = URL + "sales_tracking/user/Api.php?apicall=edit_donatur";
    public static final String URL_CREATE_DONATUR = URL + "sales_tracking/user/Api.php?apicall=create_donatur";
    public static final String URL_CREATE_ACCOUNT = URL + "sales_tracking/user/Api.php?apicall=create_akun";
    public static final String URL_CHECK_STATUS = URL + "sales_tracking/user/Api.php?apicall=check_status";
    public static final String URL_UPDATE_STATUS = URL + "sales_tracking/user/Api.php?apicall=update_status_donatur";

    //Db Sales Tracking
    public static final String URL_INSERT_CALL= URL + "sales_tracking/post/insertdata.php?api=call";
    public static final String URL_INSERT_VISIT= URL + "sales_tracking/post/insertdata.php?api=visit";
    public static final String URL_INSERT_CLOSING= URL + "sales_tracking/post/insertdata.php?api=closing";
    public static final String GET_DATA_DONATUR= URL + "sales_tracking/getDataDonatur/dataDonatur.php";
    public static final String GET_LIST_CALL= URL + "sales_tracking/post/listcall.php";
    public static final String GET_LIST_VISIT= URL + "sales_tracking/post/listVisit.php";
    public static final String GET_LIST_CLOSING= URL + "sales_tracking/post/listClosing.php";

    public static final String URL_UPLOAD_IMAGE= URL + "sales_tracking/upload_image/upload-image-to-server.php";

    public static final String URL_SERVER= URL + "sales_tracking/upload_image/upload/";
}
