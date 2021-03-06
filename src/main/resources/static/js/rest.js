
function loadMenu(lunchDom, dinnerDom){
    var load = function(data, menuName){

        var targetTbody = $("table[name=" + menuName +"]").find("tbody");
        targetTbody.append("<tr class='table-primary' name='title'></tr>");
        $.each(data["menus"], function(i, menu){
            var title = "메뉴" + (i + 1);
            targetTbody.find("tr[name=title]").append("<td>" + title + "</td>");
            $.each(menu, function(index, value){
                var domName = "menu-" + index;
                var dom = "<tr name='" + domName + "'></tr>";

                var domElement = targetTbody.find("tr[name=" + domName + "]");
                if(domElement.length == 0){
                    targetTbody.append(dom);
                    domElement = targetTbody.find("tr[name=" + domName + "]");
                }
                domElement.append("<td name=" + menuName + ">" + value + "</td>");
            });
        });
    }

    $.ajax({
        cache: false,
        url : "/api/menu?type=lunch",
        type : "GET",

        success : function(data){
            load(data, "lunch");
            $.ajax({
                cache: false,
                url : "/api/menu?type=dinner",
                type : "GET",
                success : function(data){
                    load(data, "dinner");
                },
                error : function(xhr, status){
                    alert(xhr + " : " + status);
                }
            });
        },
        error : function(xhr, status){
            alert(xhr + " : " + status);
        }
    });
}

function uploadMenuFile(){
    console.log("upload Ready");
    var form = $('#fileUploadForm')[0];
    var data = new FormData(form);
    $.ajax({
        url : "/api/menu",
        type : "POST",
        enctype : "multipart/form-data",
        data : data,
        cache : false,
        timeout : 6000,
        processData: false,
        contentType: false,
        success : function(data){
            alert(data);
            window.location.reload();
        },
        error : function(xhr, status){
            alert(xhr + " : " + status);
        }
    });
}