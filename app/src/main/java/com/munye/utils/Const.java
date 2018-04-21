package com.munye.utils;

import android.location.LocationManager;
import android.net.ConnectivityManager;

/**
 * Created by Akash on 1/16/2017.
 */

public class Const {

    //public static final String URL = "http://192.168.43.79/munye/public/";
    public static final String URL = "http://jimmiejob.com/index.php/";
    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String USER_TYPE = "1";
    public static final String MANUAL = "0";
    public static final String VERSION = "0.0.0";
    public static final String ERROR_CODE_PRIFIX = "error_";
    public static final String COLOR_CODE_PRIFIX = "color_";
    public static final String BACKGROUND_COLOR_CODE_PRIFIX = "background_";
    public static final String CURRENT_LATITUDE = "current_latitude";
    public static final String CURRENT_LONGITUDE = "current_longitude";
    public static final int SESSON_EXPIRE_ERROR = 12;
    public static final int PENDING_PAYMENT = 19;


    //For choose action of photos...
    public static final int CHOOSE_PHOTO = 110;
    public static final int TAKE_PHOTO = 111;


    //Constant for permission request....
    public static final int PERMISSION_STORAGE_REQUEST_CODE = 210;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 211;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 213;


    /*Start activity for result request and request codes*/
    public static final int QUOTE_ACTION = 112;
    public static final int ACTION_DELETE_JOB = 113;
    public static final int ACTION_ACCEPT_QUOTE = 114;
    public static final int ACTION_DELETE_QUOTE = 115;
    public static final int ACTIVE_JOB_ACTION = 116;
    public static final int ACTION_CANCEL_JOB = 117;
    public static final int ACTION_FEEDBACK = 118;
    public static final int PREVIOUS_JOB_ACTION = 119;
    public static final int ACTION_RETRY_REQUEST = 120;
    public static final int ACTION_LOCATION_SOURCE_SETTINGS = 121;
    public static final int ACTION_SETTINGS = 122;


    /*Intent filter event for gps and internet receiver*/
    public static final String GPS_ACTION = LocationManager.PROVIDERS_CHANGED_ACTION;
    public static final String INTERNET_ACTION = ConnectivityManager.CONNECTIVITY_ACTION;

    public class ServiceType{

        private static final String HOST_URL = "http://jimmiejob.com/index.php/";
        private static final String BASE_URL = HOST_URL + "client/";
        public static final String REGISTER = BASE_URL + "register";
        public static final String LOGIN = BASE_URL + "login";
        public static final String GETPROVIDER = BASE_URL + "getproviders";
        public static final String GETERROR = HOST_URL + "arr";
        public static final String GET_PROVIDER_TYPE = HOST_URL + "application/types";
        public static final String CREATE_REQUEST = BASE_URL + "createrequest";
        public static final String VIEW_QUOTES = BASE_URL + "viewquotes";
        public static final String DELETE_JOB = BASE_URL + "deletejob";
        public static final String DELETE_QUOTE = BASE_URL + "deletequote";
        public static final String ACCEPT_QUOTE = BASE_URL + "acceptquote";
        public static final String VIEW_UPDATE_QUOTE = BASE_URL + "viewupdatedquotes";
        public static final String ACTIVE_REQUEST = BASE_URL + "activerequest";
        public static final String PREVIOUS_JOB = BASE_URL + "history";
        public static final String CANCEL_JOB = BASE_URL + "cancelconfirmedjob";
        public static final String FEEDBACK = BASE_URL + "setrate";
        public static final String RETRY_REQUEST = BASE_URL + "retryrequest";
        public static final String LOGOUT = BASE_URL + "logout";
        public static final String UPDATE_PROFILE = BASE_URL + "updateprofile";
        public static final String ADD_CARD = BASE_URL + "addcard";
        public static final String GET_CARD = BASE_URL + "getcard";
        public static final String DELETE_CARD = BASE_URL + "deletecard";
        public static final String GET_PROFILE = BASE_URL + "getprofile";
        public static final String PAY_OLD_REQUEST = BASE_URL + "payoldrequest";
        public static final String FORGOT_PASSWORD = HOST_URL + "application/forgotpassword";
        public static final String GET_SETTINGS = HOST_URL + "application/getsettings";

    }



    public class ServiceCode{

        public static final int REGISTER = 1;
        public static final int LOGIN = 2;
        public static final int GET_COMPANY_LIST = 3;
        public static final int GET_TYPES = 4;
        public static final int CREATE_REQUEST = 5;
        public static final int VIEW_QUOTES = 6;
        public static final int DELETE_JOB = 7;
        public static final int DELETE_QUOTE = 8;
        public static final int ACCEPT_QUOTE = 9;
        public static final int VIEW_UPDATE_QUOTE = 10;
        public static final int ACTIVE_REQUEST = 11;
        public static final int PREVIOUS_JOB = 12;
        public static final int CANCEL_JOB = 13;
        public static final int FEEDBACK = 14;
        public static final int RETRY_REQUEST = 15;
        public static final int LOGOUT = 16;
        public static final int UPDATE_PROFILE = 17;
        public static final int FORGOT_PASSWORD = 18;
        public static final int ADD_CARD = 19;
        public static final int GET_CARD = 20;
        public static final int DELETE_CARD = 21;
        public static final int GET_PROFILE = 22;
        public static final int PAY_OLD_REQUEST = 23;
        public static final int GET_SETTINGS = 24;

    }


    public class Params{
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String DISTANCE = "distance";
        public static final String DISTANCE_UNIT = "distance_unit";
        public static final String PASS = "password";
        public static final String CONTACT_NO = "phone";
        public static final String COUNTRY_CODE = "phone_code";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String TOKEN = "token";
        public static final String DEVICE_TYPE = "device_type";
        public static final String ID = "id";
        public static final String LOGIN_BY = "login_by";
        public static final String APP_VERSION = "app_version";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROVIDER_TYPE = "type";
        public static final String PICTURE = "picture";
        public static final String IMAGE = "image";
        public static final String ADDRESS = "address";
        public static final String JOB_TITLE = "job_title";
        public static final String JOB_TYPE = "job_type";
        public static final String DESCRIPTION = "description";
        public static final String TYPES = "types";
        public static final String REQUEST_ID = "request_id";
        public static final String PROVIDER_ID = "provider_id";
        public static final String TIME_ZONE = "time_zone";
        public static final String PAGE = "page";
        public static final String RATING = "rating";
        public static final String COMMENT = "comment";
        public static final String NEW_PASS = "new_password";
        public static final String OLD_PASS = "old_password";
        public static final String USER_TYPE = "user_type";
        public static final String PAYMENT_TOKEN = "payment_token";
        public static final String CARD_NO = "card_number";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String CVV="cvv";
        public static final String EXPDATE="expirydate";

    }

    public class httpRequestType{
        public static final String POST = "POST";
        public static final String GET = "GET";
    }


    public class JobRequestType{
        public static final int TYPE_REPAIR_MAINTENANCE = 0;
        public static final int TYPE_INSTALLATION = 1;
    }


    public class PushStatus{
        public static final String PUSH_MESSAGE = "push_message";
        public static final String PUSH_STATUS_INTENT = "push_intent";
        public static final String PUSH_ID = "push_id";
        public static final String REQUEST_ID = "request_id";
        public static final int PROVIDER_ON_THE_WAY = 1;
        public static final int PROVIDER_ARRIVE = 2;
        public static final int PROVIDER_START_JOB = 3;
        public static final int JOB_DONE = 4;
        public static final int ADD_QUOTE = 5;
        public static final int CANCEL_QUOTE = 6;
        public static final int PROVIDER_CANCEL_JOB = 7;
    }


    public class RequestStatus{
        public static final int PROVIDER_CONFIRM = 1;
        public static final int ON_THE_WAY = 2 ;
        public static final int ARRIVED = 3;
        public static final int JOB_STARTED = 4;
        public static final int JOB_DONE = 5;
        public static final int CUSTOMER_RATED = 6;
        public static final int TRADESMAN_RATED = 5;
        public static final int CANCEL_BY_USER = 8;
        public static final int CANCEL_BY_PROVIDER = 9;
    }


    public class NotificationId{
        public static final int ADD_QUOTE = 1;
        public static final int CANCEL_QUOTE = 2;
        public static final int TRADESMAN_STATUS = 3;
        public static final int CANCEL_JOB = 4;
    }
}
