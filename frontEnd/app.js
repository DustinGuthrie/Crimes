$(document).ready(function(){
  statsPage.init();
});

var title = "";
var chatData = [];
var currentUser = "";
var loggedInUser = "";

var statsPage = {

  init: function(){
      statsPage.initStyling();
      statsPage.initEvents();
    },

  initStyling: function(){
    statsPage.grabStatsFromServer();
    statsPage.grabChatsFromServer();
  },

  initEvents: function(){
    // LOG IN FUNCTIONALITY
      $('#logInButton').on('click', function(event) {
        // event.preventDefault();
        console.log("login clicked");
        $username = $('text[id="username"]').val(),
        $password = $('text[id="password"]').val,
        //post ajax
        statsPage.loggedInUser = $username;
        console.log(loggedInUser);
        statsPage.setUser($username, $password);
        $('.statMain').removeClass('hidden');
        $('.loginPage').addClass('hidden');
        $('#lineChart').removeClass('hidden');
        $('#chat').removeClass('hidden');
        $('.responses').removeClass('hidden');
        $('#sidebar-wrapper').addClass('hidden');
        //  We need to do a post with this information
        // Then dispay in top nav bar Welcome Username!  Logout
        // statsPage.setUser($username, $password);
    }),

    $('#guestUser').on('click', function(event) {
      // event.preventDefault();
      console.log("GuestUser clicked");
      $username = "Guest",
      $password = "password",
      // default value = Guest
      // default password = password
      statsPage.loggedInUser = "Guest";
      statsPage.setUser($username, $password);
  }),

  // SUBMIT CHAT MESSAGE
  $('#chatHere').on('click', function(event) {
  //  event.preventDefault();
   statsPage.createNewMessage();
   }),

   // OPEN REPLY TEXTAREA
   $('#chat').on('click', 'button[name="reply"]', function(event) {
   //  event.preventDefault();
    $('textarea[name="reply"]').removeClass('hidden');
    $('#replyHere').removeClass('hidden');
  }),

  // REPLY TO A MESSAGE
  $('#replyHere').on('click', function(event) {
    // event.preventDefault();
    statsPage.replyMessage();
  })

   // DELETE CHAT MESSAGE
   $('#chat').on('click', 'button[name="delete"]', function(event) {
     event.preventDefault();
     $(this).parent('article').html('');
   }),

    // CHOOSING A STATE & YEAR
    $('#stateYearButton').on('click', function(event){
      console.log("this is happening - initialbutton");
      event.preventDefault();
      var state = $('select[name="state"]').val();
      var year = $('select[name="year"]').val();

      $.ajax({
        method: 'GET',
        url: '/home',
        success: function(crime) {
          console.log("SUCCESS: " + state + year, JSON.parse(crime));
          stateCrimeData = JSON.parse(crime);

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
              $('#colorado').addClass('hidden');
              $('#chat').removeClass('hidden');
              $('#mcgruff').removeClass('hidden');
            }
          });
        },
      failure: function(crime) {
        console.log("FAILURE");
      }
    });
  });
},

  // GET STAT TABLE FROM SERVER
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
          }
        });
      },
      failure: function(crime) {
        console.log("FAILURE: ", crime);
      }
    });
  },

  // LOAD STAT TABLE TO DOM
  loadStats: function(data) {
    var statsHTML = "";
        var statsTemplateCurrUser = _.template($('#statsTmplCurrUser').html());
        statsHTML += statsTemplateCurrUser(data);
        $('.statMain').html(statsHTML);
  },

  // LOAD CHATS TO DOM
  loadChats: function(data) {
    event.preventDefault();
    var chatHTML = "";
    _.each(chatData, function(el, idx, arr) {
      chatHTML += chatTemplate(el);
    });
    $('.responses').prepend(chatHTML);
  },

  // SET USER
  setUser: function(name, password){
    $.ajax({
      method: "POST",
      url: '/login',
      data: {username: name, password: password},
      success: function(data) {
        if (data) {
          console.log("IVE BEEN SUCCESSED", data);
          // Here we need to look at what is being sent back and determine what we want to save as variable(s)
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
    statsPage.loggedInUser = name;
  },

  // GET CHAT MESSAGES FROM SERVER
  grabChatsFromServer: function(data) {
    $.ajax({
      method: 'GET',
      url: '',
      success: function(data){
        console.log("SUCCESS, " + data);
        statsPage.loadChats(data);
      },
      failure: function(data){
        console.log("FAILURE, " + data);
      }
    });
  },

  // CREATE NEW CHAT MESSAGES FOR DOM
  createNewMessage: function(data) {
    // var avatarURL: $('input[name="avatar"]').val();
    var usernameText = $('input[name="username"]').val();
    var contentText = $('textarea[name="chat"]').val();
    var chosenState = $('select[name="state"]').val();
    var chosenYear = $('select[name="year"]').val();
    // var level =
    var newChatMessage = {
      // avatar: avatarURL,
      username: usernameText,
      content: contentText,
      state: chosenState,
      year: chosenYear,
      mainTopic: true,
      // level: 1
    };
    chatData.push(newChatMessage);
    var chatTemplate = _.template($('#chatTmpl').html());
    var chatHTML = chatTemplate(newChatMessage);
    $('.responses').prepend(chatHTML);
    $('textarea').val('');
    console.log(chatHTML);
  },

  // SEND NEW CHAT MESSAGES TO SERVER
  sendChatsToServer: function(newChatMessage) {
    $.ajax({
      method: 'POST',
      url: '',
      data: newChatMessage,
      success: function(bitterData){
        console.log("SUCCESS ");
      },
      failure: function(bitterData){
        console.log("FAILURE ");
      }
    });
  },

  // REPLY TO A MESSAGE ON DOM
  replyMessage: function(data) {
    // var avatarURL: $('input[name="avatar"]').val();
    var usernameText = $('input[name="username"]').val();
    var contentText = $('textarea[name="reply"]').val();
    var chosenState = $('select[name="state"]').val();
    var chosenYear = $('select[name="year"]').val();
    // var level = 1;
    // var replyLevel = level + "-" + level++
    var newReplyMessage = {
      // avatar: avatarURL,
      username: usernameText,
      content: contentText,
      state: chosenState,
      year: chosenYear
      // mainTopic: false,
      // level: replyLevel
    };
    chatData.push(newReplyMessage);
    var replyTemplate = _.template($('#replyTmpl').html());
    var replyHTML = replyTemplate(newReplyMessage);
    $('.responses').append(replyHTML);
    $('textarea').val('');
    $('textarea[name="reply"]').addClass('hidden');
    $('#replyHere').addClass('hidden');
  },

  // DELETE MESSAGES FROM DOM
  deleteChatMessage: function() {
    // event.preventDefault();
    // var msgID = $(this).closest('.newChat').data(_id);
    // console.log(msgID);
      $(this).closest('.newChat').remove();
      // statsPage.deleteChatsFromServer(msgID);
  },

  // DELETE MESSAGES FROM SERVER
  // deleteChatsFromServer: function(msgID) {
  //   $.ajax({
  //     method: 'DELETE',
  //     url: "" + "/" + msgID,
  //     success: function (data) {
  //       console.log("DELETE SUCCESS " + data);
  //       // SOMETHING ELSE NEEDS TO HAPPEN HERE
  //     },
  //     failure: function (data) {
  //       console.log("DELETE FAILURE " + data)
  //     }
  //   })
  // },

  url: "/home",

};
