class RootView extends View {
    initialize() {
        super.initialize();
        var environment = Hummer.env;

        this.style = {
            width: environment.deviceWidth,
            height: environment.deviceHeight,
        };

        var input = new Input("input");
        input.style = {
            backgroundColor: "#cccccccc",
            color: "#ff0000",
            fontSize: 32,
            // textAlign: "right",
            placeholderColor: '#999999',
            cursorColor: "#00ff00",
            maxLength: 10,
            textAlign: "left",
            returnKeyType: 'send',
            width: 600
        };
        input.placeholder = "请输入单行文本";
        input.focus = true;
        // input.text = "xxx";
        input.addEventListener('input', e => {
            console.log(e.state + "");
            console.log(e.text);
        })

        var textArea = new TextArea("textArea");
        textArea.style = {
            height: 200,
            marginTop: 30,
            fontSize: 32,
            textAlign: "left",
            textLineClamp: 3,
            returnKeyType: 'next',
            backgroundColor: "#cccccccc",
            width: 600
        };
        textArea.placeholder = "请输入多行文本";
        // input.text = "xxx";
        textArea.addEventListener('input', e => {
            console.log("textArea");
            console.log(e.state + "");
            console.log(e.text);
        })
        this.appendChild(input);
        this.appendChild(textArea);
    }
}

Hummer.render(new RootView('rootid'));