function goLogin(){
    var formData = $("form[name=loginForm]").serialize();
    $.ajax({
            cache: false,
            url : "/login",
            type: "POST",
            data : formData,
            success : function(data){
                if(data == "Login Success")
                {
                    window.location="/";
                }
                else{
                    alert("login failed");
                }
            },
            error: function(xhr, status){
                alert(xhr + " : " + status);
            }
    });
}

function goLogout(){
    var formData = $("form[name=logoutForm]").serialize();
    $.ajax({
        cache: false,
        url : "/logout",
        type : "POST",
        data : formData,
        success : function(data){
            alert("메인 페이지로 이동합니다.");
            window.location="/";
        },
        error : function(xhr, status){
            alert(xhr + " : " + status);
        }
    });
}