package foregg.foreggserver.domain.enums;

public enum NavigationType {
    daily_hugg_graph,
    calendar_graph,
    inj_med_info_screen,  // inj_med_info_screen/INJECTION/{id}/{time}  or inj_med_info_screen/MEDICINE/{id}/{time}
    account_graph,
    create_daily_hugg,
    reply_daily_hugg,   //reply_daily_hugg/{데일리 허그 생성날짜}
    myChallenge,
    challenge_support,  //challenge_support/{id}
    home_graph
}
