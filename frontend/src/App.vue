<template>
  <div class="wrapper">
    <div id="nav">
      <div class="user-info" v-if="isLoggedIn">
        <div class="user-image-container" @mouseover="showEditTooltip = true" @mouseleave="showEditTooltip = false"
          @click="toggleEditForm">
          <img v-if="userImage" :src="userImage" alt="User Image" class="user-portrait" />
          <span v-if="showEditTooltip" class="edit-tooltip">Do you want to change you profile picture?</span>
          <p class="name">{{ userName }}</p>
        </div>
      </div>
      <button class="nav-btn">
        <router-link v-bind:to="{ name: 'home' }" :class="getLinkClass('home')">HOME</router-link>&nbsp;
      </button>
      <!-- <div class="separator"></div> -->
      <button class="nav-btn">
        <router-link v-bind:to="{ name: 'forums' }" :class="getLinkClass('forums')">FORUMS</router-link>&nbsp;
      </button>
      <!-- <div v-if="this.isLoggedIn" class="separator"></div> -->
      <button v-if="isLoggedIn" class="nav-btn">
        <router-link v-bind:to="{ name: 'favorites' }" v-if="userName"
          :class="getLinkClass('favorites')">FAVORITES</router-link>&nbsp;
      </button>
      <button v-if="isLoggedIn" class="nav-btn">
        <router-link v-bind:to="{ name: 'messages' }" v-if="userName"
          :class="getLinkClass('messages')">MESSAGES</router-link>&nbsp;
      </button>
      <!-- <div v-if="this.isLoggedIn" class="separator"></div> -->
      <button v-if="isLoggedIn" class="nav-btn">
        <router-link v-bind:to="{ name: 'logout' }" v-if="$store.state.token != ''"
          class="router-link-nonactive">LOGOUT</router-link>
      </button>
      <!-- <div v-if="!this.isLoggedIn" class="separator"></div> -->
      <button v-if="!this.isLoggedIn" class="nav-btn">
        <router-link v-bind:to="{ name: 'login' }" v-if="$store.state.token == ''"
          class="router-link-nonactive">LOGIN</router-link>
      </button>
      <div class="logo-container">
        <img src="/images/POST-UP_logo.png" alt="Logo" class="logo" />
      </div>
    </div>

    <!-- form for update user image -->
    <div v-if="editFormVisible" class="edit-form">
      <form @submit.prevent="updateUserDetails">
        <div class="form-group">
          <label for="userImage">Profile Image URL</label>
          <input type="text" ref="myInput" v-model="updatedUserImage" placeholder="Enter new image URL" />
        </div>
        <div class="edit-pic-btns">
          <button class="pic-save" type="submit">Save</button>
          <button class="pic-cancel" type="button" @click="toggleEditForm">Cancel</button>
        </div>
      </form>
    </div>


    <WidgetContainerModal />

    <!-- <video autoplay loop muted id="capstone-app">
    <source src="/images/city.mp4" type="video/webm">
    <source src="/images/city.mp4" type="video/mp4">
  </video> -->

    <router-view />
  </div>
</template>
<script>
import UserService from './services/UserService.js';
import ModeratorService from './services/ModeratorService';
import { container } from 'jenesius-vue-modal'

export default {

  components: { WidgetContainerModal: container },
  name: "App",
  data() {
    return {
      showEditTooltip: false,
      editFormVisible: false,
      updatedUserImage: '',
      role: this.$store.getters.role,
      isDragging: false,
      dragStartX: 0,
      dragStartY: 0,
      offsetX: 0,
      offsetY: 0,
      role: this.$store.getters.role,
    };
  },
  created() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.user_image) {
      const sanitizedImageUrl = user.user_image.trim().replace(/"/g, '');
      this.$store.commit("SET_USER_IMAGE", sanitizedImageUrl);
    }
  },
  computed: {
    // Get the user's name from Vuex
    userName() {
      return this.$store.getters.username;
    },
    // Compute whether the user is logged in
    isLoggedIn() {
      return this.$store.state.token !== '';
    },
    userImage() {
      return this.$store.getters.userImage;
    },
  },
  methods: {
    getLinkClass(routeName) {
      return this.$route.name === routeName ? 'router-link-active' : 'router-link-nonactive';
    },
    toggleEditForm() {
      this.editFormVisible = !this.editFormVisible;
    },
    updateUserDetails() {
      const sanitizedImageUrl = this.updatedUserImage.trim().replace(/"/g, '');
      const updates = JSON.stringify(sanitizedImageUrl);
      UserService.updateUserDetails(this.$store.getters.userId, updates)
        .then(() => {
          this.$store.commit('SET_USER_IMAGE', this.updatedUserImage);
          alert('User details updated successfully.');
          this.toggleEditForm(); // Close the form after saving
        }).catch(error => {
          console.error('Error updating user details:', error);
          alert('Failed to update user details.');
        });
    }

  },
  watch: {
    // Watch the editFormVisible property
    editFormVisible(newVal) {
      if (newVal) {
        // Use $nextTick to ensure the input is in the DOM before trying to focus it
        this.$nextTick(() => {
          if (this.$refs.myInput) {
            this.$refs.myInput.focus();
          }
        });
      }
    }
  }
};
</script>

<style>
.wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background-color: var(--secondary);
  align-items: center
}

::after,
::before {
  box-sizing: border-box;
}

body,
html {
  scrollbar-color: var(--primary) var(--secondary);
  font-family: "Work Sans", sans-serif;
  font-weight: 400;
  line-height: 1.5;
  color: #888;
  background-color: var(--secondary);
  text-align: left;
  height: 100%;
  min-height: 100%;
  margin: 0;
}

body {
  padding: 1%;
}

:root {
  --primary: #e50914 ;
  --secondary: #0f0f0f;
  --nero: #181818;
}

#nav {
  display: flex;
  justify-content: space-evenly;
  width: 100%;
  z-index: 1000;
  padding: 10px;
  margin-bottom: 0px;
}

.logo-container {
  top: 0;
  right: 20px;
  height: calc(2 * 65px);
  height: calc(2 * 65px);
  /* Adjust '60px' to match the height of your nav */
  display: flex;
  align-items: center;
}

.logo {
  height: 100%;
  object-fit: contain;
  /* Ensures the logo scales nicely */
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  cursor: pointer;
}

.name {
  position: absolute;
  bottom: -12px;
  color: white;
  background-color: rgba(0, 0, 0, 0.733);
  width: 100%;
  padding: 3px 7px;
  text-align: center;
  border-radius: 10px;
  /* border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px; */
  transform: translateY(50%);
}

.name {
  position: absolute;
  bottom: -12px;
  color: #888;
  background-color: rgba(0, 0, 0, 0.733);
  width: 100%;
  padding: 3px 7px;
  text-align: center;
  border-radius: 10px;
  transform: translateY(50%);
}

.user-portrait {
  width: calc(2 * 60px);
  height: calc(2 * 60px);
  border-radius: 50%;
  object-fit: cover;
  z-index: 0;
}

.edit-tooltip {
  position: absolute;
  top: 0;
  left: 60px;
  color: #888;
  background-color: rgba(0, 0, 0, 0.733);
  padding: 5px;
  border-radius: 8px;
  font-size: 12px;
  white-space: nowrap;
}

.edit-form {
  position: fixed;
  justify-content: center;
  display: flex;
  top: 100px;
  left: 0;
  right: 65%;
  margin: 0 auto;
  max-width: 15%;
  background: radial-gradient(circle, var(--nero) 55%, rgba(0, 0, 0, 0.699));
  border: 1px solid black;
  padding: 20px;
  padding-bottom: 10px;
  padding-bottom: 10px;
  border-radius: 5px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, .7);
  z-index: 10;
  cursor: move;
  transition: transform 0.1s ease-out;
  cursor: move;
  transition: transform 0.1s ease-out;
}

.form-group label {
  display: block;
  display: flex;
  justify-content: center;
  margin-bottom: 5px;
}

.form-group input {
  display: flex;
  justify-content: center;
  background-color: rgb(139, 138, 138);
  width: 100%;
  margin-top: 15px;
  margin-right: 15px;
  padding: 5px;
  border: 2px solid #000000;
  border-radius: 8px;
  font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
}
.form-group input:focus{
  outline:none;
}

.edit-pic-btns {
  display: flex;
  margin-top: 15px;
  margin-bottom: 5px;
  justify-content: space-between;
}

.pic-save,
.pic-cancel {
  background-color: rgb(248, 133, 29) !important;
  transition: transform 0.3s ease-in-out;
  border-radius: 5px;
  width: 70px;
  padding: 5px;
  color: white;
}

.pic-save:hover,
.pic-cancel:hover {
  background-color: rgb(185, 98, 22) !important;
  ;
}

.pic-save:hover,
.pic-cancel:hover {
  background-color: rgb(185, 98, 22) !important;
  ;

}

/* #capstone-app {
  font-family: Arial, Helvetica, sans-serif;
  background: radial-gradient(circle, rgb(255, 166, 107) 35%, rgba(240, 107, 19, 0.927));
  padding-left: 2vw;
  padding-right: 2vw;
  margin-top: 0px;
  padding-top: 10px;
  margin-bottom: 0px;
  padding-bottom: 8vh;
} */

/* #capstone-app {
  font-family: Arial, Helvetica, sans-serif;
  z-index: -1000;
  left: 50%;
  width: 50%;
  width: 100vw;
  height: 100vh;
  top: 50%;
  position: absolute;
  background-size: cover;
  -webkit-transform: translate(-50%, -50%);
  -moz-transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  object-fit: cover;
  background-position: center;
} */

.nav-btn {
  background-color: transparent;
  color: white;
  border: none;
  font-size: 25px;
  padding: 5px;
  transition: transform 0.3s ease-in-out;
  /* Smooth transition for hover effects */
}

router-view {
  position: relative;
  z-index: 1;
  /* Ensure the router-view content appears above the video */
}

.nav-btn .router-link-active,
.router-link-exact-active {
  text-decoration: none;
  font-size: 30px;
  color: var(--primary);
  transition: .3s ease-in-out;
}

.router-link-nonactive {
  text-decoration: none;
  color: #888;
  transition: .3s ease-in-out;
}

#logged-in {
  color: black;
}

.router-link-nonactive:hover {
  text-decoration: none;
  transform: scaleX(3);
  transition: transform 0.3s ease-in-out;
}

.separator {
  align-content: center;
}

.post-author {
  color: black;
  text-decoration: none;
}
</style>