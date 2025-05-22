import { useState, useContext, useRef, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { ThemeContext } from '../contexts/ThemeContext';
import './ChatSupport.css';
import axios from 'axios';

const HF_API_URL = process.env.REACT_APP_HF_API_URL;
const HF_MODEL_NAME = process.env.REACT_APP_HF_MODEL_NAME;
const HF_API_TOKEN = process.env.REACT_APP_HF_API_TOKEN;

const ChatSupport = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === 'dark';

  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const toggleChat = () => {
    setIsOpen(!isOpen);
    if (!isOpen && messages.length === 0) {
      addMessage('AI', 'Hello! How can I help you today?');
    }
  };

  const handleInputChange = (event) => {
    setNewMessage(event.target.value);
  };

  const handleSendMessage = async () => {
    if (newMessage.trim()) {
      const userMessage = newMessage;
      addMessage('You', userMessage);
      setNewMessage('');
      setIsTyping(true);

      try {
        const systemInstruction = `You are a helpful and secure AI assistant for a modern online banking application.
          Your purpose is to guide users and provide information about the app's features and how to navigate them.

          Here are the main sections and functionalities of the banking application:
          - **Dashboard:** Users can see an overview of their accounts, current balance, and recent transactions.
          - **Transactions:** A dedicated page to view a detailed history of all financial transactions.
          - **Beneficiaries:** Users can manage and add recipients for transfers.
          - **Transfer:** This section allows users to initiate and complete fund transfers to other accounts or beneficiaries.
          - **Profile:** Users can view and update their personal information.
          - **Global Search:** A search bar is available for logged-in users to find information or navigate within the app.
          - **Login/Register:** For accessing or creating an account.
          - **Admin Sections:** If a user is an administrator, they have specific dashboards for managing users, viewing all transactions, and their own profile.

          **CRITICAL RULES:**
          1.  **NO PERSONAL DATA:** You MUST NOT ask for, process, store, or attempt to retrieve any personal or sensitive information like account numbers, passwords, PINs, security questions, full names, addresses, or phone numbers.
          2.  **NO DIRECT ACTIONS:** You CANNOT directly perform actions like checking balances, initiating transfers, resetting passwords, or managing accounts. Guide the user on how to do these within the secure app.
          3.  **REFER TO APP SECTIONS:** Always direct users to the relevant section of the banking application (e.g., "To check your balance, please visit your Dashboard").
          4.  **SECURITY:** If a user asks for sensitive account-specific actions, always advise them to log in to their secure online banking portal or mobile app, or to contact human customer support for assistance.
          5.  **CONCISE:** Keep your responses clear, helpful, and to the point.
          6.  **UNSURE:** If you don't know the answer or if the question is outside the scope of banking app features, politely state that you cannot help with that specific query and suggest contacting human support.

          Begin by greeting the user and offering assistance.`;

        let conversationPrompt = `<s>[INST] ${systemInstruction} [/INST]`;

        messages.forEach(msg => {
          if (msg.sender === 'You') {
            conversationPrompt += ` [INST] ${msg.text} [/INST]`;
          } else {
            conversationPrompt += ` ${msg.text}</s>`;
          }
        });

        conversationPrompt += ` [INST] ${userMessage} [/INST]`;

        const response = await axios.post(
          `${HF_API_URL}${HF_MODEL_NAME}`,
          {
            inputs: conversationPrompt,
            parameters: {
              max_new_tokens: 250,
              temperature: 0.7,
              top_p: 0.9,
              do_sample: true,
              return_full_text: false
            }
          },
          {
            headers: { Authorization: `Bearer ${HF_API_TOKEN}` },
          }
        );

        let aiResponseText = "I am currently unable to provide a response.";

        if (response.data && response.data[0] && response.data[0].generated_text) {
            const generatedText = response.data[0].generated_text;
            aiResponseText = generatedText.trim();
            const cleanRegex = /^(?:<s>)?(?:\[INST\].*?\[\/INST\])?\s*(.*?)(?:<\/s>)?$/s;
            const cleanMatch = aiResponseText.match(cleanRegex);
            if (cleanMatch && cleanMatch[1]) {
                aiResponseText = cleanMatch[1].trim();
            }
            if (!aiResponseText || aiResponseText.length < 5) {
                aiResponseText = "I am currently unable to provide a helpful response. Please try rephrasing your question or contact human support.";
            }
        }
        addMessage('AI', aiResponseText);

      } catch (error) {
        console.error('Error getting AI response:', error);
        if (error.response) {
          if (error.response.status === 429) {
            addMessage('AI', 'I am currently experiencing high traffic. Please try again in a moment.');
          } else if (error.response.status === 503) {
            addMessage('AI', 'The AI model is currently unavailable or busy. Please try again in a few seconds.');
          } else if (error.response.data && error.response.data.error) {
              addMessage('AI', `AI Error: ${error.response.data.error}. Please try again later.`);
          } else {
            addMessage('AI', `An API error occurred: ${error.response.status}. Please try again.`);
          }
        } else if (error.request) {
          addMessage('AI', 'Network Error: Could not reach the AI service. Please check your internet connection.');
        } else {
          addMessage('AI', 'Oops! An unexpected error occurred while getting an AI response. Please try again.');
        }
      } finally {
        setIsTyping(false);
      }
    }
  };

  const addMessage = (sender, text) => {
    setMessages((prevMessages) => [...prevMessages, { sender, text }]);
  };

  return (
    <div className={`chat-support-container ${isDark ? 'dark-theme' : 'light-theme'}`}>
      <button className={`chat-toggle-button ${isOpen ? 'open' : ''} ${isDark ? 'btn-dark' : 'btn-primary'}`} onClick={toggleChat}>
        {isOpen ? 'Close Chat' : 'Chat with AI'}
      </button>

      {isOpen && (
        <div className={`chat-window ${isDark ? 'bg-secondary text-light border-secondary' : 'bg-white border-primary'}`}>
          <div className="chat-header">AI Support</div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div key={index} className={`message ${msg.sender === 'You' ? 'user' : 'ai'}`}>
                <strong>{msg.sender}:</strong> {msg.text}
              </div>
            ))}
            {isTyping && (
              <div className="message ai typing-indicator">
                <strong>AI:</strong> <span className="dot">.</span><span className="dot">.</span><span className="dot">.</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
          <div className="chat-input">
            <input
              type="text"
              className={`form-control ${isDark ? 'bg-dark text-light border-secondary' : ''}`}
              placeholder="Type your message..."
              value={newMessage}
              onChange={handleInputChange}
              onKeyPress={(event) => event.key === 'Enter' && handleSendMessage()}
              disabled={isTyping}
            />
            <button
              className={`btn ${isDark ? 'btn-outline-light' : 'btn-outline-primary'} ms-2`}
              onClick={handleSendMessage}
              disabled={isTyping || !newMessage.trim()}
            >
              Send
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatSupport;