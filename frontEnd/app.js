$(document).ready(function(){
  statsPage.init();
});

var title = "";

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
        event.preventDefault();
        console.log("login clicked");
         $('.statMain').removeClass('hidden');
         $('.loginPage').addClass('hidden');
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

          var graphArr = [];
          var graphState = _.each(stateCrimeData, function(el, idx, array) {
            if(state == el.abbrev) {
                // for(var i = 0; i < stateCrimeData; )
                title = el.name;
                return title;

            }
          });



          var states = _.each(stateCrimeData, function(el, idx, array) {
            if(state == el.abbrev && year == el.year) {
              var stateStats = {population: el.population, total: el.total, name: el.name, robbery: el.robbery, rape: el.rape, assault: el.assault, murder: el.murder};
              statsPage.loadStats(stateStats);
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

          if(el.name === "National" && el.year === 2012){
            var nationalStats = {population: el.population, total: el.total, name: el.name, robbery: el.robbery, rape: el.rape, assault: el.assault, murder: el.murder};
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

  // loadGraphs: function(data) {
  //   var graphsHTML = "";
  //       var graphsTemplateCurrUser = _.template($('#graphsTmplCurrUser').html());
  //       graphsHTML += graphsTemplateCurrUser(data);
  //       $('#lineChart').html(graphsHTML);
  // },

  // }

  url: "/home",

};
