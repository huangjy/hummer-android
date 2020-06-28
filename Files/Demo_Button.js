class RootView extends View {
    initialize() {
        super.initialize();
        var environment = Hummer.env;

        this.style = {
            width: environment.deviceWidth,
            height: environment.deviceHeight,
        };

        var button = new Button();
        button.pressed = { backgroundColor: '#333333', color: '#00FF00' };
        button.style = {
            backgroundColor: '#666666', color: '#FF0000' 
        };
        button.addEventListener('tap', (e) => {
            console.log('remove all callbacks');
        })
        // button.disabled = true;
        button.text = "I am button";
        this.appendChild(button);
    }
}

Hummer.render(new RootView('rootid'));