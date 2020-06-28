class RootView extends View {
    initialize() {
        super.initialize();
        var environment = Hummer.env;

        this.style = {
            width: environment.deviceWidth,
            height: environment.deviceHeight,
        };

        var view = new View();
        view.style = {
            flexDirection: 'row',
            backgroundColor: "#ff0022",
            justifyContent: 'flex-start',
            alignItems: 'center',
            width: 800,
            height: 600
        };
        this.appendChild(view);

        var view1 = new View();
        view1.style = {
            height: 80,
            marginTop: 30,
            backgroundColor: 'linear-gradient(45deg #00ff60 #0000ff)',
            width: 200,
        };
        view.appendChild(view1);

        var view2 = new View();
        view2.style = {
            height: 80,
            marginTop: 30,
            opacity: 0.5,
            backgroundColor: "#00ff00",
            width: 200,
        };
        view.appendChild(view2);

        var view3 = new View();
        view3.style = {
            height: 80,
            marginTop: 30,
            backgroundColor: "#0000ff",
            borderColor: '#ffffff',
            borderWidth: 2,
            borderRadius: 10,
            borderStyle: 'solid',
            width: 200,
        };
        view.appendChild(view3);
    }
}

Hummer.render(new RootView('rootid'));

