
function loadMenu(lunchDom, dinnerDom){
    var load = function(data, menuName){

        console.log(data["menus"]);

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