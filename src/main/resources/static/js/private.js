Date.prototype.format = function (format)
{
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
        (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}
$(function ()
{
    $("#loginButton").click(function ()
    {
        login();
    });

    $("#loginYzm").keyup(function (event)
    {
        if (event.which == 13)
            login();
    });

    $("#registerButton").click(function ()
    {
        var uname = $("#registerUname").val();
        var upwd = $("#registerUpwd").val();
        var confirmPwd = $("#registerconfirmUpwd").val();

        var flag = true;

        if (uname.length > 16)
        {
            $("#registerUname+span").html("X").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerUname+span").html("").css("background-color", "#EEEEEE");

        if ((upwd.length > 16) || (upwd.length < 6))
        {
            $("#registerUpwd+span").html("密码长度需6-16位").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerUpwd+span").html("").css("background-color", "#EEEEEE");

        if (upwd != confirmPwd)
        {
            $("#registerconfirmUpwd+span").html("X").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerconfirmUpwd+span").html("").css("background-color", "#EEEEEE");



        if (flag)
        {
            var data = {"uname": uname, "upwd": upwd};
            $.post("/register.do", data, function (data)
            {
                alert(data);
                if (data == "注册成功")
                    location.reload()
            });
        }
        else
        {
            if (uname.length > 16) {
                alert("用户名不能超过16个字符");
            } else if ((upwd.length > 16) || (upwd.length < 6)) {
                alert("密码长度必须在6-16位之间");
            } else if (upwd != confirmPwd) {
                alert("两次输入的密码不一致");
            }
        }
    });
    $("#logouot").click(function ()
    {
        $.get("/logout.do", {}, function (data)
        {
            alert(data);
            if (data == "退出成功")
                location.reload()
        });
    });


    $("#person").click(function ()
    {
        window.location.href = '/person.do';
    });


    $("#sendPostButton").click(function ()
    {
        var ptitle = $("#sendPostTitle").val();
        var pbody = $("#sendPostBody").val();
        var category = $("#sendPostCategory").val();
        if ((ptitle.length > 0 && ptitle.length <= 30) && (pbody.length > 0 && pbody.length < 1000) && category !== "")
        {
            var data = {"ptitle": ptitle, "pbody": pbody, "category": category};
            $.post("/sendPost.do", data, function (data)
            {
                alert(data);
                if (data == "发送成功")
                    location.reload()
            });
        }
        else
            alert("注意字数和板块必须选择");
    });
    $("#sendReply").click(function ()
    {
        var replyMessage = $("#replyMessage").val();
        var pid = $("#pid").val();
        if (replyMessage.length > 0 && replyMessage.length <= 1000)
        {
            var data = {"replymessage": replyMessage, "post.pid": pid};
            $.post("/reply.do", data, function (data)
            {
                alert(data);
                if (data == "回帖成功")
                    location.reload()
            });
        }
        else
            alert("注意字数");
    })

    $("#deleteReply").click(function ()
    {
        var url = $(this).parent().attr("href");
        $.get(url, function (data)
        {
            alert(data)
            if (data == "删除成功")
                location.reload()
        })
    });

    $("#updatePwd").click(function ()
    {
        var personNewPwd = $("#personNewPwd").val();
        var personConfirmPwd = $("#personConfirmPwd").val();
        if (personNewPwd.length > 6 && personNewPwd.length <= 16)
            if (personNewPwd == personConfirmPwd)
                $("#updateForm").submit();
            else
            {
                alert("两次输入的密码不相符");
                return false;
            }
        else
        {
            alert("新密码长度长度(6,16]")
            return false;
        }

    });

});

function banUser(uid)
{
    $.get("/ban/" + uid, function (data)
    {
        alert(data)
        if (data == "禁言成功")
            location.reload();
    });
}

function unbanUser(uid)
{
    $.get("/ban/" + uid, function (data)
    {
        alert(data)
        if (data == "禁言成功")
            location.reload();
    });
}

function addFavorite(pid)
{
    $.get("/favorite/add/" + pid, function (data)
    {
        alert(data);
        if (data == "添加收藏成功" || data.indexOf("成功") > -1)
            location.reload();
    });
}

function removeFavorite(pid)
{
    $.get("/favorite/remove/" + pid, function (data)
    {
        alert(data);
        if (data == "取消收藏成功" || data.indexOf("成功") > -1)
            location.reload();
    });
}

function toggleSticky(pid, action)
{
    var actionText = action === 'sticky' ? '置顶' : '取消置顶';
    if (confirm('确定要' + actionText + '这个帖子吗？')) {
        $.get("/toggleSticky/" + pid + "/" + action, function (data)
        {
            alert(data);
            if (data.indexOf("成功") > -1)
                location.reload();
        });
    }
}

function login()
{
    var uname = $("#loginUname").val();
    var upwd = $("#loginUpwd").val();
    if (uname == "" || upwd == "")
        return;
    else
    {
        var data = {"uname": uname, "upwd": upwd};
        $.post("/login.do", data, function (data)
        {
            alert(data);
            if (data == "登录成功")
                location.reload();
            else
            {
                // 登录失败
            }
        });
    }
}

/*]]>*/