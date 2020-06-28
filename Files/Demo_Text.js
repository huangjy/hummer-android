class RootView extends View {
    initialize() {
        super.initialize();
        var environment = Hummer.env;

        this.style = {
            width: environment.deviceWidth,
            height: environment.deviceHeight,
        };

        var text1 = new Text();
        text1.style = {
            textAlign: "center",
            backgroundColor: "#ff000022",
            textDecoration: "underline",
            color: "#FF0000",
            fontWeight: "bold",
            fontSize: 16,
        };
        text1.text = "1234567890";

        var text2 = new Text();
        text2.style = {
            marginTop: 30,
            textAlign: "center",
            backgroundColor: "#ff000022",
            fontSize: 16,
        };
        text2.formattedText = '我是<font color=\"#FF0000\">红色</font>的';

        var text3 = new Text();
        text3.style = {
            marginTop: 30,
            textAlign: "center",
            backgroundColor: "#ff000022",
            fontSize: 16,
            textOverflow: 'ellipsis',
            textLineClamp: 2,
        };
        text3.text = '我是超长文字我是超长文字我是超长文字我是超长文字我是超长文字我是超长文字我是超长文字我是超长文字我是超长文字';

        this.appendChild(text1);
        this.appendChild(text2);
        this.appendChild(text3);
    }
}

Hummer.render(new RootView('rootid'));

