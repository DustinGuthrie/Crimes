$(document).ready(function(){
  statsPage.init();
});

var title = "";
var currentUser = "";

var statsPage = {
  init: function(){
      statsPage.initStyling();
      statsPage.initEvents();
    },
  initStyling: function(){
    statsPage.grabStatsFromServer();

  },
  initEvents: function(){
    // LOG IN FUNCTIONALITY
      $('#logInButton').on('click', function(event) {
        // event.preventDefault();
        console.log("login clicked");
          $username = $('text[id="loginUserName"]').val(),
          $password = $('text[id="password"]').val,
        //post ajax
        statsPage.setUser($username, $password);
        //  $('.statMain').removeClass('hidden');
        //  $('.loginPage').addClass('hidden');
        //  $('#lineChart').removeClass('hidden');
        //  We need to do a post with this information
        // Then dispay in top nav bar Welcome Username!  Logout
        // statsPage.setUser($username, $password);

    }),

    $('#guestUser').on('click', function(event) {
      // event.preventDefault();
      console.log("GuestUser clicked");
      $username = "guest",
      $password = "password",
      // default value = Guest
      // default password = password
      statsPage.setUser($username, $password);

      //  $('.statMain').removeClass('hidden');
      //  $('.loginPage').addClass('hidden');
      //  $('#lineChart').removeClass('hidden');
       //  We need to do a post with this information
       // Then dispay in top nav bar Welcome Username!  Logout

  }),


    $('#chatHere').on('click', function(event) {
     event.preventDefault();
     var chatMessage = {
       content: $('textarea[name="chat"]').val(),
       username: loginUserName,
     };
     var chatTemplate = _.template('#chatTmpl');
     var chatHTML = chatTemplate(chatMessage);
     $('.responses').prepend(chatHTML);
     console.log(chatHTML);
   }),



    //*****Login page that will then bring up Home page - THECLICKHIDDENFUNCTION****
    $('#stateYearButton').on('click', function(event){
      console.log("this is happening - initialbutton");
      event.preventDefault();
      var state = $('select[name="state"]').val();
      var year = $('select[name="year"]').val();

      $.ajax({
        method: 'GET',
        // url: '/get-single',
        url: '/home',
        success: function(crime) {
          console.log("SUCCESS: " + state + year, JSON.parse(crime));
          stateCrimeData = JSON.parse(crime);

          // var graphArr = [];
          // var graphState = _.each(stateCrimeData, function(el, idx, array) {
          //   if(state == el.abbrev) {
          //       // for(var i = 0; i < stateCrimeData; )
          //       title = el.name;
          //       return title;
          //
          //   }
          // });
          var states = _.each(stateCrimeData, function(el, idx, array) {
            if(state == "CO" && year <= 2012) {
              var coloradoStats = {population: "Cant Remember", total: 0, name: "Colorado", robbery: 0, rape: 0, assault: 0, murder: 0};
              statsPage.loadStats(coloradoStats);
              $('#lineChart').addClass('hidden');
              $('#chat').addClass('hidden');
              $('#mcgruff').addClass('hidden');
              $('#colorado').removeClass('hidden');
              }
            else if(state == el.name && year == el.year) {
                  var stateStats = {population: el.population, total: el.total, name: el.abbrev, robbery: el.robbery, rape: el.rape, assault: el.assault, murder: el.murder};
                  statsPage.loadStats(stateStats);
              $('#lineChart').addClass('hidden');
              $('#colorado').addClass('hidden');
              $('#chat').removeClass('hidden');
              $('#mcgruff').removeClass('hidden');

              // statsPage.loadGraphs(stateStats);
            }
          });
        },
      failure: function(crime) {
        console.log("FAILURE");
      }
    });
  });
},


  grabStatsFromServer: function() {
    $.ajax({
      method: 'GET',
      url: '/home',
      success: function(crime) {
        console.log("SUCCESS: ", JSON.parse(crime));
        crimeData = JSON.parse(crime);
        var national = _.each(crimeData, function(el, idx, arr) {

          if(el.abbrev === "National" && el.year === 2012){
            var nationalStats = {population: el.population, total: el.total, name: el.abbrev, robbery: el.robbery, rape: el.rape, assault: el.assault, murder: el.murder};
            statsPage.loadStats(nationalStats);
            // statsPage.loadGraphs(nationalStats);
          }
        });
      },
      failure: function(crime) {
        console.log("FAILURE: ", crime);
      }
    });
  },

  loadStats: function(data) {
    var statsHTML = "";
        var statsTemplateCurrUser = _.template($('#statsTmplCurrUser').html());
        statsHTML += statsTemplateCurrUser(data);
        $('.statMain').html(statsHTML);
  },

  setUser: function(name, password){
    $.ajax({
      method: "POST",
      url: 'login',
      data: {username: name, password: password},
      success: function(data) {
        if (data === true) {
          $('.statMain').removeClass('hidden');
          $('.loginPage').addClass('hidden');
          $('#lineChart').removeClass('hidden');
        } else {
           Console.log("Something went wrong with Login")
        }
      },
      error: function(data) {
        console.log("ERROR", data);
      }
    })
    statsPage.userName = name;
  },


  // loadGraphs: function(data) {
  //   var graphsHTML = "";
  //       var graphsTemplateCurrUser = _.template($('#graphsTmplCurrUser').html());
  //       graphsHTML += graphsTemplateCurrUser(data);
  //       $('#lineChart').html(graphsHTML);
  // },

  // }

  url: "/home",

};
