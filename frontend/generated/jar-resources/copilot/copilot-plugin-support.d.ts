/**
 * Plugin API for the dev tools window.
 */
export interface CopilotInterface {
    send(command: string, data: any): void;
    addPanel(panel: PanelConfiguration): void;
}
export interface MessageHandler {
    handleMessage(message: ServerMessage): boolean;
}
export interface ServerMessage {
    /**
     * The command
     */
    command: string;
    /**
     * The data for the command
     */
    data: any;
}
export declare enum Framework {
    Flow = 0,
    HillaLit = 1,
    HillaReact = 2
}
export interface CopilotPlugin {
    /**
     * Called once to initialize the plugin.
     *
     * @param copilotInterface provides methods to interact with the dev tools
     */
    init(copilotInterface: CopilotInterface): void;
}
export declare enum MessageType {
    INFORMATION = "information",
    WARNING = "warning",
    ERROR = "error"
}
export interface Message {
    id: number;
    type: MessageType;
    message: string;
    details?: string;
    link?: string;
    persistentId?: string;
    dontShowAgain: boolean;
    deleted: boolean;
}
export interface PanelConfiguration {
    header: string;
    expanded: boolean;
    draggable: boolean;
    panel?: 'bottom' | 'left' | 'right';
    panelOrder?: number;
    tag: string;
    floating: boolean;
    floatingPosition?: {
        left: number;
        top: number;
        width: number;
        height?: number;
    };
    showOn?: Framework[];
}
