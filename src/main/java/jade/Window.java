package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The type Window.
 */
public class Window {

    private static Window window = null;
    private static Scene currentScene;
    private final float a;
    /**
     * The Width.
     */
    int width, /**
     * The Height.
     */
    height;
    /**
     * The Title.
     */
    String title;
    /**
     * The R.
     */
    float r;
    /**
     * The G.
     */
    float g;
    /**
     * The B.
     */
    float b;
    private long glfwWindow;


    private Window() {
        this.height = 1920;
        this.width = 1080;
        this.title = "Mario";
        r = b = g = a = 1;
    }

    /**
     * Change scene.
     *
     * @param newScene the new scene
     */
    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;

            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;

            default:
                assert false : "Unknown Scene '" + newScene + "'";
        }
    }

    /**
     * Get window.
     *
     * @return the window
     */
    public static Window get() {

        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    /**
     * Run.
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        //Free the memory once the loop is finished
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate the GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        //Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialise glfw");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        //Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable v-sync on
        glfwSwapInterval(1);

        //Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        Window.changeScene(0);
    }

    private void loop() {

        float beginTime = Time.getTime();
        Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            //poll events from
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) currentScene.update(dt);


            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}
