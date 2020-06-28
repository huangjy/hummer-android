class RootView extends View {
    initialize() {
        super.initialize();
        var environment = Hummer.env;

        this.style = {
            width: environment.deviceWidth,
            height: environment.deviceHeight,
        };

        var image1 = new Image();
        image1.style = {
            width: 300,
            height: 200,
            marginTop: 20,
            backgroundColor: '#ff000022',
            alignSelf: 'center',
            resize: 'contain',
        };
        image1.src = "http://www.arinchina.com/upload/portal/201703/25/180051zuwe1zpgz7727ujm.png";
        this.appendChild(image1);

    }
}

Hummer.render(new RootView('rootid'));